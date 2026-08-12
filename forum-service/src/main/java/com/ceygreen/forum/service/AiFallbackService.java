package com.ceygreen.forum.service;

import com.ceygreen.forum.kafka.ForumEventPublisher;
import com.ceygreen.forum.model.Post;
import com.ceygreen.forum.model.Reply;
import com.ceygreen.forum.repository.PostRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@ConditionalOnProperty(name = "forum.ai-fallback.enabled", havingValue = "true", matchIfMissing = true)
public class AiFallbackService {

    private static final Logger log = LoggerFactory.getLogger(AiFallbackService.class);
    
    private final PostRepository postRepository;
    private final MongoTemplate mongoTemplate;
    private final SimilarityService similarityService;
    private final ForumEventPublisher eventPublisher;
    
    @Value("${forum.ai-fallback.unanswered-threshold-hours:24}")
    private int thresholdHours;
    
    @Value("${gemini.api-key:}")
    private String geminiApiKey;
    
    @Value("${gemini.model:gemini-1.5-flash}")
    private String geminiModel;
    
    private final RestTemplate restTemplate = new RestTemplate();

    public AiFallbackService(PostRepository postRepository, 
                             MongoTemplate mongoTemplate,
                             SimilarityService similarityService,
                             ForumEventPublisher eventPublisher) {
        this.postRepository = postRepository;
        this.mongoTemplate = mongoTemplate;
        this.similarityService = similarityService;
        this.eventPublisher = eventPublisher;
    }

    @Scheduled(fixedRateString = "${forum.ai-fallback.check-interval-minutes:30}", timeUnit = java.util.concurrent.TimeUnit.MINUTES)
    public void processUnansweredPosts() {
        log.info("Starting AI fallback check for unanswered posts");
        Instant cutoff = Instant.now().minus(thresholdHours, ChronoUnit.HOURS);
        
        List<Post> posts = postRepository.findUnansweredBefore(cutoff, 50);
        log.info("Found {} posts requiring AI fallback", posts.size());
        
        for (Post post : posts) {
            try {
                handleFallback(post);
            } catch (Exception e) {
                log.error("Error processing AI fallback for post {}", post.getId(), e);
            }
        }
    }
    
    private void handleFallback(Post post) {
        post.setAiAnswerAttempted(true); // Attempted regardless of outcome
        
        Optional<Post> similar = similarityService.findSimilarResolvedPost(post);
        String replyBody;
        
        if (similar.isPresent()) {
            Post match = similar.get();
            replyBody = "This looks similar to an earlier resolved question — [link/reference to that post](/posts/" + match.getId() + ").";
            log.info("Used similarity match for post {}", post.getId());
        } else {
            if (geminiApiKey == null || geminiApiKey.isBlank()) {
                log.warn("Gemini API key is not configured, skipping LLM fallback for post {}", post.getId());
                post.setAiAnswerAttempted(false);
                return;
            }
            replyBody = callGemini(post);
            if (replyBody == null) {
                postRepository.save(post);
                return; // Failed to get response
            }
            replyBody += "\n\n⚠️ This is an AI-generated answer and has not been verified by another farmer.";
            log.info("Used Gemini for post {}", post.getId());
        }
        
        Reply reply = new Reply();
        reply.setAuthorId("SYSTEM_AI");
        reply.setAuthorName("CeyGreen AI Assistant");
        reply.setBody(replyBody);
        reply.setAiGenerated(true);
        
        post.getReplies().add(reply);
        post.setUpdatedAt(Instant.now());
        
        Post saved = postRepository.save(post);
        eventPublisher.publishNewReply(saved, reply);
    }
    
    private String callGemini(Post post) {
        String url = "https://generativelanguage.googleapis.com/v1beta/models/" + geminiModel + ":generateContent?key=" + geminiApiKey;
        
        String prompt = "You are a helpful AI assistant for greenhouse farming. Give a short, practical answer to the following question. Be explicit about uncertainty if you are not sure.\n\n" +
                "Title: " + post.getTitle() + "\n" +
                "Body: " + post.getBody() + "\n" +
                "Crop: " + post.getCropType() + "\n" +
                "Tags: " + String.join(", ", post.getTags());
                
        Map<String, Object> request = Map.of(
            "contents", List.of(
                Map.of("parts", List.of(
                    Map.of("text", prompt)
                ))
            )
        );
        
        try {
            Map response = restTemplate.postForObject(url, request, Map.class);
            if (response != null && response.containsKey("candidates")) {
                List candidates = (List) response.get("candidates");
                if (!candidates.isEmpty()) {
                    Map candidate = (Map) candidates.get(0);
                    Map content = (Map) candidate.get("content");
                    List parts = (List) content.get("parts");
                    if (!parts.isEmpty()) {
                        Map part = (Map) parts.get(0);
                        return (String) part.get("text");
                    }
                }
            }
        } catch (Exception e) {
            log.error("Gemini API call failed", e);
        }
        return null;
    }
}
