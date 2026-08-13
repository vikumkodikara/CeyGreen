package com.ceygreen.forum.repository;

import com.ceygreen.forum.model.Post;
import com.ceygreen.forum.model.Reply;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifies the dynamic filtering, sorting and pagination in {@link PostRepositoryImpl} against a
 * real MongoDB. Uses a dedicated {@code _test} database so it never touches dev data, and clears the
 * collection before each test for isolation. Requires MongoDB on localhost:27017 (present in CI via
 * the forum-service job's service container).
 */
@DataMongoTest
class PostRepositoryCustomTest {

    @DynamicPropertySource
    static void mongoProps(DynamicPropertyRegistry registry) {
        // Force a throwaway database regardless of the URI configured for the app.
        registry.add("spring.data.mongodb.uri", () -> "mongodb://localhost:27017/ceygreen_forum_test");
    }

    @Autowired
    private PostRepository postRepository;

    // Oldest -> newest: p1 (Tomato), p2 (Chili), p3 (tomato).
    private String p1;
    private String p2;
    private String p3;

    @BeforeEach
    void seed() {
        postRepository.deleteAll();
        p1 = save("Tomato", List.of("tomato", "blight"), false,
                Instant.parse("2026-01-01T00:00:00Z"), 5);
        p2 = save("Chili", List.of("chili"), true,
                Instant.parse("2026-01-02T00:00:00Z"), 10);
        p3 = save("tomato", List.of("pest"), false,
                Instant.parse("2026-01-03T00:00:00Z"), 1);
    }

    @Test
    void newestSortReturnsMostRecentFirst() {
        Page<Post> page = postRepository.search(
                new PostSearch(List.of(), null, null, PostSort.NEWEST), PageRequest.of(0, 20));

        assertThat(page.getTotalElements()).isEqualTo(3);
        assertThat(page.getContent()).extracting(Post::getId).containsExactly(p3, p2, p1);
    }

    @Test
    void cropTypeFilterIsCaseInsensitive() {
        Page<Post> page = postRepository.search(
                new PostSearch(List.of(), "tomato", null, PostSort.NEWEST), PageRequest.of(0, 20));

        // Matches both "Tomato" and "tomato", newest first.
        assertThat(page.getContent()).extracting(Post::getId).containsExactly(p3, p1);
    }

    @Test
    void tagsFilterMatchesAnyRequestedTag() {
        Page<Post> page = postRepository.search(
                new PostSearch(List.of("blight", "chili"), null, null, PostSort.NEWEST),
                PageRequest.of(0, 20));

        assertThat(page.getContent()).extracting(Post::getId).containsExactly(p2, p1);
    }

    @Test
    void resolvedFilterSelectsMatchingState() {
        Page<Post> resolved = postRepository.search(
                new PostSearch(List.of(), null, true, PostSort.NEWEST), PageRequest.of(0, 20));
        assertThat(resolved.getContent()).extracting(Post::getId).containsExactly(p2);

        Page<Post> unresolved = postRepository.search(
                new PostSearch(List.of(), null, false, PostSort.NEWEST), PageRequest.of(0, 20));
        assertThat(unresolved.getContent()).extracting(Post::getId).containsExactly(p3, p1);
    }

    @Test
    void mostUpvotedSortOrdersBySummedReplyUpvotes() {
        Page<Post> page = postRepository.search(
                new PostSearch(List.of(), null, null, PostSort.MOST_UPVOTED), PageRequest.of(0, 20));

        // p2=10, p1=5, p3=1
        assertThat(page.getContent()).extracting(Post::getId).containsExactly(p2, p1, p3);
    }

    @Test
    void paginationSlicesResultsAndReportsTotals() {
        Page<Post> first = postRepository.search(
                new PostSearch(List.of(), null, null, PostSort.NEWEST), PageRequest.of(0, 1));
        assertThat(first.getTotalElements()).isEqualTo(3);
        assertThat(first.getTotalPages()).isEqualTo(3);
        assertThat(first.getContent()).extracting(Post::getId).containsExactly(p3);

        Page<Post> second = postRepository.search(
                new PostSearch(List.of(), null, null, PostSort.NEWEST), PageRequest.of(1, 1));
        assertThat(second.getContent()).extracting(Post::getId).containsExactly(p2);
    }

    private String save(String cropType, List<String> tags, boolean resolved, Instant createdAt, int upvotes) {
        Post post = new Post();
        post.setAuthorId("author-" + cropType);
        post.setAuthorName("Author " + cropType);
        post.setTitle("Question about " + cropType);
        post.setBody("Body for " + cropType);
        post.setTags(tags);
        post.setCropType(cropType);
        post.setResolved(resolved);
        post.setCreatedAt(createdAt);
        post.setUpdatedAt(createdAt);

        Reply reply = new Reply();
        reply.setAuthorId("replier");
        reply.setAuthorName("Replier");
        reply.setBody("An answer");
        reply.setUpvotes(upvotes);
        post.getReplies().add(reply);

        return postRepository.save(post).getId();
    }
}
