package com.ceygreen.forum.service;

import com.ceygreen.forum.model.Post;
import com.ceygreen.forum.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifies {@link SimilarityService} against a real MongoDB (needs the title/body text index). Uses
 * a dedicated {@code _test} database and clears it between tests. MongoDB must be on
 * localhost:27017 (present in CI via the forum-service job's service container).
 */
@DataMongoTest
@Import(SimilarityService.class)
class SimilarityServiceTest {

    @DynamicPropertySource
    static void mongoProps(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", () -> "mongodb://localhost:27017/ceygreen_forum_test");
    }

    @Autowired
    private PostRepository postRepository;
    @Autowired
    private SimilarityService similarityService;

    @BeforeEach
    void clean() {
        postRepository.deleteAll();
    }

    @Test
    void findsResolvedMatchByCropTypeAndSharedTag() {
        save("Tomato blight solved", "Use copper fungicide", "Tomato", List.of("tomato", "blight"), true);

        Post query = queryPost("How do I treat tomato blight?", "Leaves have spots", "Tomato", List.of("tomato"));
        Optional<Post> match = similarityService.findSimilarResolvedPost(query);

        assertThat(match).isPresent();
        assertThat(match.get().getTitle()).isEqualTo("Tomato blight solved");
    }

    @Test
    void ignoresUnresolvedAndDifferentCrop() {
        save("Tomato blight solved", "Use copper fungicide", "Tomato", List.of("tomato", "blight"), false);
        save("Chili aphids solved", "Use neem oil", "Chili", List.of("chili", "aphids"), true);

        Post query = queryPost("Tomato blight?", "spots on leaves", "Tomato", List.of("tomato"));
        Optional<Post> match = similarityService.findSimilarResolvedPost(query);

        assertThat(match).isEmpty();
    }

    @Test
    void fallsBackToTextSearchWhenNoStructuredMatch() {
        save("Dealing with aphids on chili", "Neem oil works well", "Chili", List.of("chili", "aphids"), true);

        // No crop/tags on the query, so the structured match is skipped; the title keyword "aphids"
        // drives the full-text fallback.
        Post query = queryPost("Aphids everywhere on my plants", "please help", null, List.of());
        Optional<Post> match = similarityService.findSimilarResolvedPost(query);

        assertThat(match).isPresent();
        assertThat(match.get().getTitle()).isEqualTo("Dealing with aphids on chili");
    }

    @Test
    void returnsEmptyWhenNothingIsSimilar() {
        save("Watering schedule for cucumbers", "Twice a day", "Cucumber", List.of("cucumber", "water"), true);

        Post query = queryPost("Best fertilizer brand recommendations", "which one", null, List.of());
        Optional<Post> match = similarityService.findSimilarResolvedPost(query);

        assertThat(match).isEmpty();
    }

    private void save(String title, String body, String cropType, List<String> tags, boolean resolved) {
        Post p = new Post();
        p.setAuthorId("author");
        p.setAuthorName("Author");
        p.setTitle(title);
        p.setBody(body);
        p.setCropType(cropType);
        p.setTags(tags);
        p.setResolved(resolved);
        p.setCreatedAt(Instant.parse("2026-01-01T00:00:00Z"));
        postRepository.save(p);
    }

    private Post queryPost(String title, String body, String cropType, List<String> tags) {
        Post p = new Post();
        p.setAuthorId("querier");
        p.setAuthorName("Querier");
        p.setTitle(title);
        p.setBody(body);
        p.setCropType(cropType);
        p.setTags(tags);
        return p;
    }
}
