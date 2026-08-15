package com.ceygreen.forum.service;

import com.ceygreen.forum.common.ApiException;
import com.ceygreen.forum.dto.PostResponse;
import com.ceygreen.forum.dto.ReplyActionRequest;
import com.ceygreen.forum.dto.ReplyRequest;
import com.ceygreen.forum.kafka.ForumEventPublisher;
import com.ceygreen.forum.model.Post;
import com.ceygreen.forum.model.Reply;
import com.ceygreen.forum.repository.PostRepository;
import com.ceygreen.forum.security.CurrentUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

/**
 * Unit tests for reply creation and the thread actions (upvote / acceptAnswer / flag). The
 * repository and Kafka publisher are mocked, so these assert business rules only, without a
 * database.
 */
@ExtendWith(MockitoExtension.class)
class ForumServiceReplyActionTest {

    @Mock
    private PostRepository postRepository;
    @Mock
    private ForumEventPublisher eventPublisher;
    @InjectMocks
    private ForumService forumService;

    private static final CurrentUser AUTHOR = new CurrentUser("u-author", "Author", "farmer");
    private static final CurrentUser OTHER = new CurrentUser("u-other", "Other", "farmer");

    private Post post;
    private Reply reply;

    @BeforeEach
    void setup() {
        post = new Post();
        post.setId("p1");
        post.setAuthorId("u-author");
        post.setAuthorName("Author");
        post.setTitle("Why are my leaves yellow?");
        post.setBody("Details here");

        reply = new Reply();
        reply.setId("r1");
        reply.setAuthorId("u-replier");
        reply.setAuthorName("Replier");
        reply.setBody("Try adjusting the pH");
        post.getReplies().add(reply);

        lenient().when(postRepository.findById("p1")).thenReturn(Optional.of(post));
        lenient().when(postRepository.save(any(Post.class))).thenAnswer(inv -> inv.getArgument(0));
    }

    @Test
    void addReplyAppendsReplyAndPublishesEvent() {
        PostResponse res = forumService.addReply("p1", new ReplyRequest("Great answer"), OTHER);

        assertThat(post.getReplies()).hasSize(2);
        Reply added = post.getReplies().get(1);
        assertThat(added.getAuthorId()).isEqualTo("u-other");
        assertThat(added.getBody()).isEqualTo("Great answer");
        assertThat(res.replies()).hasSize(2);
        verify(eventPublisher).publishNewReply(any(Post.class), any(Reply.class));
    }

    @Test
    void addReplyRejectsBlankBody() {
        assertThatThrownBy(() -> forumService.addReply("p1", new ReplyRequest("   "), OTHER))
                .isInstanceOfSatisfying(ApiException.class,
                        e -> assertThat(e.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST));
        verifyNoInteractions(eventPublisher);
    }

    @Test
    void upvoteAddsVoteAndIsIdempotent() {
        ReplyActionRequest req = new ReplyActionRequest("upvote", "r1", null);

        forumService.applyReplyAction("p1", req, OTHER);
        assertThat(reply.getUpvotes()).isEqualTo(1);
        assertThat(reply.getUpvotedBy()).containsExactly("u-other");

        // A second vote from the same user must not double-count.
        forumService.applyReplyAction("p1", req, OTHER);
        assertThat(reply.getUpvotes()).isEqualTo(1);
        assertThat(reply.getUpvotedBy()).containsExactly("u-other");

        // Actions never emit Kafka events.
        verifyNoInteractions(eventPublisher);
    }

    @Test
    void upvoteWithoutReplyIdUpvotesPost() {
        ReplyActionRequest req = new ReplyActionRequest("upvote", null, null);
        forumService.applyReplyAction("p1", req, OTHER);
        assertThat(post.getUpvotes()).isEqualTo(1);
        assertThat(post.getUpvotedBy()).containsExactly("u-other");
    }

    @Test
    void upvoteUnknownReplyIsNotFound() {
        ReplyActionRequest req = new ReplyActionRequest("upvote", "nope", null);
        assertThatThrownBy(() -> forumService.applyReplyAction("p1", req, OTHER))
                .isInstanceOfSatisfying(ApiException.class,
                        e -> assertThat(e.getStatus()).isEqualTo(HttpStatus.NOT_FOUND));
    }

    @Test
    void acceptAnswerByAuthorResolvesPost() {
        ReplyActionRequest req = new ReplyActionRequest("acceptAnswer", "r1", null);
        forumService.applyReplyAction("p1", req, AUTHOR);

        assertThat(post.isResolved()).isTrue();
        assertThat(post.getAcceptedReplyId()).isEqualTo("r1");
    }

    @Test
    void acceptAnswerByNonAuthorIsForbidden() {
        ReplyActionRequest req = new ReplyActionRequest("acceptAnswer", "r1", null);
        assertThatThrownBy(() -> forumService.applyReplyAction("p1", req, OTHER))
                .isInstanceOfSatisfying(ApiException.class,
                        e -> assertThat(e.getStatus()).isEqualTo(HttpStatus.FORBIDDEN));
        assertThat(post.isResolved()).isFalse();
        assertThat(post.getAcceptedReplyId()).isNull();
    }

    @Test
    void flaggingReplyMarksItFlaggedAtThreshold() {
        ReplyActionRequest req = new ReplyActionRequest("flag", "r1", null);

        forumService.applyReplyAction("p1", req, OTHER);
        assertThat(reply.getFlagCount()).isEqualTo(1);
        assertThat(reply.isFlagged()).isFalse();

        forumService.applyReplyAction("p1", req, OTHER);
        forumService.applyReplyAction("p1", req, OTHER);
        assertThat(reply.getFlagCount()).isEqualTo(3);
        assertThat(reply.isFlagged()).isTrue();
    }

    @Test
    void flaggingWithoutReplyIdFlagsThePost() {
        ReplyActionRequest req = new ReplyActionRequest("flag", null, null);
        forumService.applyReplyAction("p1", req, OTHER);

        assertThat(post.getFlagCount()).isEqualTo(1);
        assertThat(post.isFlagged()).isFalse();
        assertThat(reply.getFlagCount()).isZero();
    }

    @Test
    void unknownActionIsBadRequest() {
        ReplyActionRequest req = new ReplyActionRequest("explode", "r1", null);
        assertThatThrownBy(() -> forumService.applyReplyAction("p1", req, OTHER))
                .isInstanceOfSatisfying(ApiException.class,
                        e -> assertThat(e.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST));
    }
}
