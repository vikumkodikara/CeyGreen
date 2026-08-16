package com.ceygreen.ai.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import org.springframework.http.client.ClientHttpRequestInterceptor;

@Service
@ConditionalOnProperty(name = "forum.ai-fallback.enabled", havingValue = "true", matchIfMissing = false)
public class AiFallbackService {

    private static final Logger log = LoggerFactory.getLogger(AiFallbackService.class);
    
    @Value("${forum.ai-fallback.unanswered-threshold-hours:24}")
    private int thresholdHours;
    
    @Value("${gemini.api-key:}")
    private String geminiApiKey;
    
    @Value("${gemini.model:gemini-1.5-flash}")
    private String geminiModel;

    @Value("${forum-service.url:http://forum-service:8085}")
    private String forumServiceUrl;

    @Value("${ceygreen.security.api-key:ceygreen-dev-api-key}")
    private String serviceApiKey;
    
    private final RestTemplate restTemplate;

    public AiFallbackService() {
        this.restTemplate = new RestTemplate();
        this.restTemplate.getInterceptors().add((request, body, execution) -> {
            request.getHeaders().add("X-API-Key", serviceApiKey);
            return execution.execute(request, body);
        });
    }

    @Scheduled(fixedRateString = "${forum.ai-fallback.check-interval-minutes:30}", timeUnit = java.util.concurrent.TimeUnit.MINUTES)
    public void processUnansweredPosts() {
        log.info("Starting AI fallback check for unanswered posts via Forum Service API");
        
        try {
            String url = forumServiceUrl + "/internal/posts/unanswered?hours=" + thresholdHours;
            List<Map<String, Object>> posts = restTemplate.getForObject(url, List.class);
            
            if (posts != null && !posts.isEmpty()) {
                log.info("Found {} posts requiring AI fallback", posts.size());
                for (Map<String, Object> post : posts) {
                    handleFallback(post);
                }
            }
        } catch (Exception e) {
            log.error("Error fetching unanswered posts from Forum Service", e);
        }
    }
    
    private void handleFallback(Map<String, Object> post) {
        String postId = (String) post.get("id");
        
        try {
            // Attempt to find similar
            String url = forumServiceUrl + "/internal/posts/similar";
            Map<String, Object> similar = restTemplate.postForObject(url, post, Map.class);
            String replyBody;
            
            if (similar != null && similar.containsKey("id")) {
                String matchId = (String) similar.get("id");
                replyBody = "This looks similar to an earlier resolved question — [link/reference to that post](/api/forum/posts/" + matchId + ").";
                log.info("Used similarity match for post {}", postId);
            } else {
                if (geminiApiKey == null || geminiApiKey.isBlank()) {
                    log.warn("Gemini API key is not configured, skipping LLM fallback for post {}", postId);
                    return; // Return instead of updating attempt flag via API
                }
                replyBody = callGemini(post);
                if (replyBody == null) {
                    return; // Failed to get response
                }
                replyBody += "\n\n⚠️ This is an AI-generated answer and has not been verified by another farmer.";
                log.info("Used Gemini for post {}", postId);
            }
            
            Map<String, Object> reply = Map.of(
                "authorId", "SYSTEM_AI",
                "authorName", "CeyGreen AI Assistant",
                "body", replyBody,
                "aiGenerated", true
            );
            
            restTemplate.postForLocation(forumServiceUrl + "/internal/posts/" + postId + "/replies", reply);
            
        } catch (Exception e) {
            log.error("Error processing AI fallback for post {}", postId, e);
        }
    }
    
    private String callGemini(Map<String, Object> post) {
        String url = "https://generativelanguage.googleapis.com/v1beta/models/" + geminiModel + ":generateContent?key=" + geminiApiKey;
        
        String prompt = "You are a helpful AI assistant for greenhouse farming. Give a short, practical answer to the following question. Be explicit about uncertainty if you are not sure.\n\n" +
                "Title: " + post.get("title") + "\n" +
                "Body: " + post.get("body") + "\n" +
                "Crop: " + post.get("cropType") + "\n" +
                "Tags: " + (post.get("tags") != null ? String.join(", ", (List<String>)post.get("tags")) : "");
                
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
