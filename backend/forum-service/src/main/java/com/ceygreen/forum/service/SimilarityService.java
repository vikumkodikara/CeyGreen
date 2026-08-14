package com.ceygreen.forum.service;

import com.ceygreen.forum.model.Post;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.data.mongodb.core.query.TextQuery;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Cheap, no-LLM check for whether a question resembles an already-resolved one. The AI fallback uses
 * it to avoid a Gemini call when the community has effectively answered this before.
 *
 * <p>Two strategies, cheapest first, both restricted to resolved posts and excluding the post
 * itself:
 * <ol>
 *   <li>a structured match on the same crop type plus at least one shared tag;</li>
 *   <li>a MongoDB full-text search over the title/body text index.</li>
 * </ol>
 */
@Service
public class SimilarityService {

    private final MongoTemplate mongoTemplate;

    public SimilarityService(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    /** Return a resolved post similar to the given one, or empty if none is a strong match. */
    public Optional<Post> findSimilarResolvedPost(Post post) {
        Optional<Post> structured = findByCropAndTags(post);
        if (structured.isPresent()) {
            return structured;
        }
        return findByText(post);
    }

    /** Resolved posts sharing this post's crop type and at least one tag, newest first. */
    private Optional<Post> findByCropAndTags(Post post) {
        boolean hasCrop = post.getCropType() != null && !post.getCropType().isBlank();
        boolean hasTags = post.getTags() != null && !post.getTags().isEmpty();
        if (!hasCrop && !hasTags) {
            return Optional.empty();
        }
        List<Criteria> filters = new ArrayList<>();
        filters.add(Criteria.where("resolved").is(true));
        if (post.getId() != null) {
            filters.add(Criteria.where("id").ne(post.getId()));
        }
        if (hasCrop) {
            filters.add(Criteria.where("cropType").regex("^" + Pattern.quote(post.getCropType()) + "$", "i"));
        }
        if (hasTags) {
            filters.add(Criteria.where("tags").in(post.getTags()));
        }
        Query query = new Query(new Criteria().andOperator(filters.toArray(new Criteria[0])))
                .with(Sort.by(Sort.Direction.DESC, "createdAt"))
                .limit(1);
        return Optional.ofNullable(mongoTemplate.findOne(query, Post.class));
    }

    /** Highest-scoring resolved post matching any of this post's title/tag keywords. */
    private Optional<Post> findByText(Post post) {
        String[] keywords = keywords(post);
        if (keywords.length == 0) {
            return Optional.empty();
        }
        TextCriteria textCriteria = TextCriteria.forDefaultLanguage().matchingAny(keywords);
        TextQuery query = TextQuery.queryText(textCriteria).sortByScore();
        query.addCriteria(Criteria.where("resolved").is(true));
        if (post.getId() != null) {
            query.addCriteria(Criteria.where("id").ne(post.getId()));
        }
        query.limit(1);
        return mongoTemplate.find(query, Post.class).stream().findFirst();
    }

    /** Distinct lower-cased tokens from the title (words > 2 chars) plus the tags. */
    private String[] keywords(Post post) {
        Set<String> terms = new LinkedHashSet<>();
        if (post.getTitle() != null) {
            Arrays.stream(post.getTitle().toLowerCase().split("\\W+"))
                    .filter(w -> w.length() > 2)
                    .forEach(terms::add);
        }
        if (post.getTags() != null) {
            post.getTags().stream()
                    .filter(t -> t != null && !t.isBlank())
                    .map(String::toLowerCase)
                    .forEach(terms::add);
        }
        return terms.toArray(new String[0]);
    }
}
