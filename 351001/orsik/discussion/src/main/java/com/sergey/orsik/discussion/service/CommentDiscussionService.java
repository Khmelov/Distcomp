package com.sergey.orsik.discussion.service;

import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.sergey.orsik.discussion.cassandra.CommentByIdRow;
import com.sergey.orsik.discussion.cassandra.CommentByTweetKey;
import com.sergey.orsik.discussion.cassandra.CommentByTweetRow;
import com.sergey.orsik.discussion.client.PublisherTweetClient;
import com.sergey.orsik.discussion.exception.EntityNotFoundException;
import com.sergey.orsik.discussion.repository.CommentByIdRepository;
import com.sergey.orsik.discussion.repository.CommentByTweetRepository;
import com.sergey.orsik.discussion.util.CommentIds;
import com.sergey.orsik.dto.request.CommentRequestTo;
import com.sergey.orsik.dto.response.CommentResponseTo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class CommentDiscussionService {

    private final CommentByIdRepository commentByIdRepository;
    private final CommentByTweetRepository commentByTweetRepository;
    private final PublisherTweetClient publisherTweetClient;
    private final CassandraTemplate cassandraTemplate;
    private final String cassandraKeyspace;

    public CommentDiscussionService(
            CommentByIdRepository commentByIdRepository,
            CommentByTweetRepository commentByTweetRepository,
            PublisherTweetClient publisherTweetClient,
            CassandraTemplate cassandraTemplate,
            @Value("${spring.cassandra.keyspace-name:distcomp}") String cassandraKeyspace) {
        this.commentByIdRepository = commentByIdRepository;
        this.commentByTweetRepository = commentByTweetRepository;
        this.publisherTweetClient = publisherTweetClient;
        this.cassandraTemplate = cassandraTemplate;
        this.cassandraKeyspace = cassandraKeyspace;
    }

    public List<CommentResponseTo> findAll(int page, int size, String sortBy, String sortDir, Long tweetId, String content) {
        List<CommentResponseTo> collected = new ArrayList<>();
        if (tweetId != null) {
            for (CommentByTweetRow r : commentByTweetRepository.findByKeyTweetId(tweetId)) {
                collected.add(fromTweetRow(r));
            }
        } else {
            // Listing all comments would require a full-table scan on tbl_comment_by_id (timeouts, load).
            // Filtered listing is supported only when tweetId is provided (partition read on tbl_comment_by_tweet).
        }
        List<CommentResponseTo> rows = collected;
        if (StringUtils.hasText(content)) {
            String needle = content.toLowerCase();
            rows = collected.stream()
                    .filter(r -> r.getContent() != null && r.getContent().toLowerCase().contains(needle))
                    .toList();
        }
        rows = new ArrayList<>(rows);
        rows.sort(comparator(sortBy, sortDir));
        int from = Math.max(0, page * size);
        int to = Math.min(from + size, rows.size());
        if (from >= rows.size()) {
            return List.of();
        }
        return rows.subList(from, to);
    }

    public CommentResponseTo findById(Long id) {
        CommentByIdRow row = commentByIdRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Comment", id));
        return fromIdRow(row);
    }

    public CommentResponseTo create(CommentRequestTo request) {
        publisherTweetClient.requireTweetExists(request.getTweetId());
        long id = nextUniqueId();
        Instant created = request.getCreated() != null ? request.getCreated() : Instant.now();
        CommentByIdRow byId = new CommentByIdRow(id, request.getTweetId(), request.getContent(), created);
        CommentByTweetRow byTweet = new CommentByTweetRow(
                new CommentByTweetKey(request.getTweetId(), created, id),
                request.getContent());
        commentByIdRepository.save(byId);
        commentByTweetRepository.save(byTweet);
        return fromIdRow(byId);
    }

    public CommentResponseTo update(Long id, CommentRequestTo request) {
        CommentByIdRow existing = commentByIdRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Comment", id));
        publisherTweetClient.requireTweetExists(request.getTweetId());

        CommentByTweetKey oldTweetKey = new CommentByTweetKey(existing.getTweetId(), existing.getCreated(), existing.getId());
        commentByTweetRepository.deleteById(oldTweetKey);

        Instant created = request.getCreated() != null ? request.getCreated() : existing.getCreated();
        CommentByIdRow updated = new CommentByIdRow(id, request.getTweetId(), request.getContent(), created);
        CommentByTweetRow newTweetRow = new CommentByTweetRow(
                new CommentByTweetKey(request.getTweetId(), created, id),
                request.getContent());
        commentByIdRepository.save(updated);
        commentByTweetRepository.save(newTweetRow);
        return fromIdRow(updated);
    }

    public void deleteById(Long id) {
        CommentByIdRow existing = commentByIdRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Comment", id));
        CommentByTweetKey tweetKey = new CommentByTweetKey(existing.getTweetId(), existing.getCreated(), existing.getId());
        commentByTweetRepository.deleteById(tweetKey);
        commentByIdRepository.deleteById(id);
    }

    public void deleteAllByTweetId(Long tweetId) {
        List<CommentByTweetRow> rows = commentByTweetRepository.findByKeyTweetId(tweetId);
        if (rows.isEmpty()) {
            return;
        }
        for (CommentByTweetRow row : rows) {
            commentByIdRepository.deleteById(row.getKey().getId());
        }
        SimpleStatement clearPartition = SimpleStatement.builder(
                        "DELETE FROM " + cassandraKeyspace + ".tbl_comment_by_tweet WHERE tweet_id = ?")
                .addPositionalValue(tweetId)
                .build();
        cassandraTemplate.execute(clearPartition);
    }

    private long nextUniqueId() {
        long id = CommentIds.newId();
        while (commentByIdRepository.existsById(id)) {
            id = CommentIds.newId();
        }
        return id;
    }

    private CommentResponseTo fromIdRow(CommentByIdRow r) {
        return new CommentResponseTo(r.getId(), r.getTweetId(), r.getContent(), r.getCreated());
    }

    private CommentResponseTo fromTweetRow(CommentByTweetRow r) {
        CommentByTweetKey k = r.getKey();
        return new CommentResponseTo(k.getId(), k.getTweetId(), r.getContent(), k.getCreated());
    }

    private Comparator<CommentResponseTo> comparator(String sortBy, String sortDir) {
        String field = StringUtils.hasText(sortBy) ? sortBy : "id";
        Comparator<CommentResponseTo> cmp = switch (field) {
            case "tweetId" -> Comparator.comparing(CommentResponseTo::getTweetId, Comparator.nullsLast(Long::compareTo));
            case "content" -> Comparator.comparing(CommentResponseTo::getContent, Comparator.nullsLast(String::compareToIgnoreCase));
            case "created" -> Comparator.comparing(CommentResponseTo::getCreated, Comparator.nullsLast(Comparator.naturalOrder()));
            default -> Comparator.comparing(CommentResponseTo::getId, Comparator.nullsLast(Long::compareTo));
        };
        if ("desc".equalsIgnoreCase(sortDir)) {
            cmp = cmp.reversed();
        }
        return cmp;
    }
}
