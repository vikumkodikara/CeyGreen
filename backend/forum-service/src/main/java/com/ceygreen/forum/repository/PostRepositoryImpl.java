package com.ceygreen.forum.repository;

import com.ceygreen.forum.model.Post;
import org.bson.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * {@link MongoTemplate}-based implementation of {@link PostRepositoryCustom}. The filter set is
 * dynamic (any subset of tags/cropType/resolved) and "most upvoted" needs an aggregation that sums
 * the embedded {@code replies.upvotes}, neither of which fits a derived query method.
 *
 * <p>The class name must remain {@code PostRepositoryImpl} — Spring Data resolves custom fragments
 * by convention ({repository-interface-name}Impl).
 */
public class PostRepositoryImpl implements PostRepositoryCustom {

    private final MongoTemplate mongoTemplate;

    public PostRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Page<Post> search(PostSearch search, Pageable pageable) {
        Criteria criteria = buildCriteria(search);
        long total = mongoTemplate.count(Query.query(criteria), Post.class);
        List<Post> content;
        if (search.sort() == PostSort.MOST_UPVOTED) {
            content = findByUpvotes(criteria, pageable);
        } else if (search.sort() == PostSort.TRENDING) {
            content = findTrending(criteria, pageable);
        } else {
            content = findNewest(criteria, pageable);
        }
        return new PageImpl<>(content, pageable, total);
    }

    private Criteria buildCriteria(PostSearch search) {
        List<Criteria> filters = new ArrayList<>();
        if (search.tags() != null && !search.tags().isEmpty()) {
            // Match posts carrying ANY of the requested tags.
            filters.add(Criteria.where("tags").in(search.tags()));
        }
        if (search.cropType() != null && !search.cropType().isBlank()) {
            // Exact but case-insensitive match on the whole crop type.
            filters.add(Criteria.where("cropType").regex("^" + Pattern.quote(search.cropType()) + "$", "i"));
        }
        if (search.resolved() != null) {
            filters.add(Criteria.where("resolved").is(search.resolved()));
        }
        return filters.isEmpty()
                ? new Criteria()
                : new Criteria().andOperator(filters.toArray(new Criteria[0]));
    }

    private List<Post> findNewest(Criteria criteria, Pageable pageable) {
        Query query = Query.query(criteria)
                .with(Sort.by(Sort.Direction.DESC, "createdAt"))
                .skip(pageable.getOffset())
                .limit(pageable.getPageSize());
        return mongoTemplate.find(query, Post.class);
    }

    private List<Post> findByUpvotes(Criteria criteria, Pageable pageable) {
        // $addFields totalUpvotes = sum of the embedded replies' upvotes, then order by it. Emitted
        // as a raw stage to avoid ambiguity in the fluent builder; totalUpvotes is transient and is
        // ignored when the result maps back onto Post.
        AggregationOperation addTotalUpvotes = context -> new Document("$addFields",
                new Document("totalUpvotes", new Document("$sum", "$replies.upvotes")));
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(criteria),
                addTotalUpvotes,
                Aggregation.sort(Sort.by(Sort.Direction.DESC, "totalUpvotes")
                        .and(Sort.by(Sort.Direction.DESC, "createdAt"))),
                Aggregation.skip(pageable.getOffset()),
                Aggregation.limit(pageable.getPageSize()));
        return mongoTemplate.aggregate(aggregation, mongoTemplate.getCollectionName(Post.class), Post.class)
                .getMappedResults();
    }

    private List<Post> findTrending(Criteria criteria, Pageable pageable) {
        // Trending score = upvotes + views + size(replies)
        AggregationOperation addTrendingScore = context -> new Document("$addFields",
                new Document("trendingScore", new Document("$add", java.util.Arrays.asList(
                        new Document("$ifNull", java.util.Arrays.asList("$upvotes", 0)),
                        new Document("$ifNull", java.util.Arrays.asList("$views", 0)),
                        new Document("$size", new Document("$ifNull", java.util.Arrays.asList("$replies", java.util.Collections.emptyList())))
                ))));

        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(criteria),
                addTrendingScore,
                Aggregation.sort(Sort.by(Sort.Direction.DESC, "trendingScore")
                        .and(Sort.by(Sort.Direction.DESC, "createdAt"))),
                Aggregation.skip(pageable.getOffset()),
                Aggregation.limit(pageable.getPageSize()));

        return mongoTemplate.aggregate(aggregation, mongoTemplate.getCollectionName(Post.class), Post.class)
                .getMappedResults();
    }

    @Override
    public List<Post> findUnansweredBefore(java.time.Instant cutoff, int limit) {
        Criteria criteria = new Criteria().andOperator(
                Criteria.where("aiAnswerAttempted").is(false),
                Criteria.where("createdAt").lt(cutoff),
                Criteria.where("replies").not().elemMatch(Criteria.where("isAiGenerated").is(false))
        );
        Query query = Query.query(criteria)
                .with(Sort.by(Sort.Direction.ASC, "createdAt"))
                .limit(limit);
        return mongoTemplate.find(query, Post.class);
    }
}
