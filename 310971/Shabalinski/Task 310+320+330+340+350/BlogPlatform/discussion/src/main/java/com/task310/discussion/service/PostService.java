package com.task310.discussion.service;

import com.task310.discussion.dto.PostRequestTo;
import com.task310.discussion.dto.PostResponseTo;
import com.task310.discussion.exception.EntityNotFoundException;
import com.task310.discussion.exception.ValidationException;
import com.task310.discussion.mapper.PostMapper;
import com.task310.discussion.model.Post;
import com.task310.discussion.model.PostKey;
import com.task310.discussion.model.PostState;
import com.task310.discussion.repository.PostRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.data.cassandra.core.cql.CqlOperations;
import org.springframework.data.cassandra.core.cql.CqlTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class PostService {
    private static final Logger logger = LoggerFactory.getLogger(PostService.class);
    private final PostRepository postRepository;
    private final PostMapper mapper;
    private final CassandraTemplate cassandraTemplate;
    private final CqlSession cqlSession;
    private static final AtomicLong idGenerator = new AtomicLong(System.currentTimeMillis());

    @Autowired
    public PostService(PostRepository postRepository, PostMapper mapper, CassandraTemplate cassandraTemplate, CqlSession cqlSession) {
        this.postRepository = postRepository;
        this.mapper = mapper;
        this.cassandraTemplate = cassandraTemplate;
        this.cqlSession = cqlSession;
    }

    public PostResponseTo create(PostRequestTo dto) {
        validatePostRequest(dto);
        Post post = mapper.toEntity(dto);
        
        // Generate ID
        Long newId = idGenerator.incrementAndGet();
        PostKey key = new PostKey(dto.getArticleId(), newId);
        post.setKey(key);
        
        // Set initial state to PENDING
        post.setState(PostState.PENDING);
        
        LocalDateTime now = LocalDateTime.now();
        post.setCreated(now);
        post.setModified(now);
        
        Post saved = postRepository.save(post);
        return mapper.toResponseDto(saved);
    }

    public List<PostResponseTo> findAll() {
        try {
            logger.debug("Fetching all posts from repository");
            // Use CqlSession directly to avoid StatementBuilder issue
            String cql = "SELECT * FROM distcomp.tbl_post";
            ResultSet resultSet = cqlSession.execute(cql);
            List<Post> posts = new java.util.ArrayList<>();
            for (Row row : resultSet) {
                posts.add(mapRowToPost(row));
            }
            logger.debug("Found {} posts", posts != null ? posts.size() : 0);
            if (posts == null || posts.isEmpty()) {
                return java.util.Collections.emptyList();
            }
            List<PostResponseTo> result = mapper.toResponseDtoList(posts);
            logger.debug("Mapped {} posts to DTOs", result != null ? result.size() : 0);
            return result;
        } catch (Exception e) {
            logger.error("Error fetching posts from database", e);
            throw new RuntimeException("Error fetching posts from database: " + e.getMessage(), e);
        }
    }

    public PostResponseTo findById(Long id) {
        if (id == null || id <= 0) {
            throw new ValidationException("Invalid post id");
        }
        
        // In Cassandra, we need articleId to find a post efficiently
        // For now, we'll search all posts (not optimal, but works)
        String cql = "SELECT * FROM distcomp.tbl_post";
        ResultSet resultSet = cqlSession.execute(cql);
        List<Post> allPosts = new java.util.ArrayList<>();
        for (Row row : resultSet) {
            allPosts.add(mapRowToPost(row));
        }
        Post post = allPosts.stream()
                .filter(p -> p.getId() != null && p.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Post not found with id: " + id));
        
        return mapper.toResponseDto(post);
    }

    public PostResponseTo update(Long id, PostRequestTo dto) {
        if (id == null || id <= 0) {
            throw new ValidationException("Invalid post id");
        }
        validatePostRequestForUpdate(dto);
        
        // Find existing post
        String cql = "SELECT * FROM distcomp.tbl_post";
        ResultSet resultSet = cqlSession.execute(cql);
        List<Post> allPosts = new java.util.ArrayList<>();
        for (Row row : resultSet) {
            allPosts.add(mapRowToPost(row));
        }
        Post existing = allPosts.stream()
                .filter(p -> p.getId() != null && p.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Post not found with id: " + id));
        
        Long oldArticleId = existing.getArticleId();
        Long newArticleId = dto.getArticleId();
        
        // Update articleId if changed
        if (!oldArticleId.equals(newArticleId)) {
            // Need to delete old and create new in Cassandra
            String deleteCql = "DELETE FROM distcomp.tbl_post WHERE article_id = ? AND id = ?";
            com.datastax.oss.driver.api.core.cql.PreparedStatement deleteStatement = cqlSession.prepare(deleteCql);
            cqlSession.execute(deleteStatement.bind(oldArticleId, id));
            logger.info("Deleted post with old articleId={}, id={}", oldArticleId, id);
            
            // Update the key
            PostKey newKey = new PostKey(newArticleId, id);
            existing.setKey(newKey);
        }
        
        // Update content and modified time
        existing.setContent(dto.getContent());
        LocalDateTime now = LocalDateTime.now();
        existing.setModified(now);
        
        // Save using CqlSession to avoid StatementBuilder issue
        Instant modifiedInstant = now.atZone(ZoneId.systemDefault()).toInstant();
        String insertCql = "INSERT INTO distcomp.tbl_post (article_id, id, content, state, created, modified) VALUES (?, ?, ?, ?, ?, ?)";
        com.datastax.oss.driver.api.core.cql.PreparedStatement insertStatement = cqlSession.prepare(insertCql);
        
        Instant createdInstant = existing.getCreated() != null 
            ? existing.getCreated().atZone(ZoneId.systemDefault()).toInstant()
            : Instant.now();
        
        cqlSession.execute(insertStatement.bind(
            existing.getArticleId(),
            existing.getId(),
            existing.getContent(),
            existing.getState() != null ? existing.getState().name() : "PENDING",
            createdInstant,
            modifiedInstant
        ));
        
        logger.info("Updated post with id={}, articleId={}", id, existing.getArticleId());
        return mapper.toResponseDto(existing);
    }

    public void delete(Long id) {
        if (id == null || id <= 0) {
            throw new ValidationException("Invalid post id");
        }
        
        String cql = "SELECT * FROM distcomp.tbl_post";
        ResultSet resultSet = cqlSession.execute(cql);
        List<Post> allPosts = new java.util.ArrayList<>();
        for (Row row : resultSet) {
            allPosts.add(mapRowToPost(row));
        }
        Post post = allPosts.stream()
                .filter(p -> p.getId() != null && p.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Post not found with id: " + id));
        
        // Delete using CqlSession to avoid StatementBuilder issue
        String deleteCql = "DELETE FROM distcomp.tbl_post WHERE article_id = ? AND id = ?";
        com.datastax.oss.driver.api.core.cql.PreparedStatement deleteStatement = cqlSession.prepare(deleteCql);
        cqlSession.execute(deleteStatement.bind(post.getArticleId(), post.getId()));
        logger.info("Deleted post with id={}, articleId={}", id, post.getArticleId());
    }

    public List<PostResponseTo> getPostsByArticleId(Long articleId) {
        if (articleId == null || articleId <= 0) {
            throw new ValidationException("Invalid article id");
        }
        return mapper.toResponseDtoList(postRepository.findByArticleId(articleId));
    }

    private void validatePostRequest(PostRequestTo dto) {
        if (dto == null) {
            throw new ValidationException("Post data is required");
        }
        if (dto.getId() != null) {
            throw new ValidationException("Id must not be provided in request body");
        }
        if (dto.getContent() == null || dto.getContent().trim().isEmpty()) {
            throw new ValidationException("Content is required");
        }
        if (dto.getContent().trim().length() < 2) {
            throw new ValidationException("Content must be at least 2 characters long");
        }
        if (dto.getArticleId() == null || dto.getArticleId() <= 0) {
            throw new ValidationException("Valid articleId is required");
        }
    }

    private void validatePostRequestForUpdate(PostRequestTo dto) {
        if (dto == null) {
            throw new ValidationException("Post data is required");
        }
        // Id is ignored in update - it comes from path variable
        if (dto.getContent() == null || dto.getContent().trim().isEmpty()) {
            throw new ValidationException("Content is required");
        }
        if (dto.getContent().trim().length() < 2) {
            throw new ValidationException("Content must be at least 2 characters long");
        }
        if (dto.getArticleId() == null || dto.getArticleId() <= 0) {
            throw new ValidationException("Valid articleId is required");
        }
    }

    private Post mapRowToPost(Row row) {
        Post post = new Post();
        PostKey key = new PostKey(
            row.getLong("article_id"),
            row.getLong("id")
        );
        post.setKey(key);
        post.setContent(row.getString("content"));
        if (row.getString("state") != null) {
            try {
                post.setState(PostState.valueOf(row.getString("state")));
            } catch (IllegalArgumentException e) {
                logger.warn("Invalid state value: {}", row.getString("state"));
                post.setState(PostState.PENDING);
            }
        }
        // Convert Instant to LocalDateTime
        if (row.getInstant("created") != null) {
            post.setCreated(LocalDateTime.ofInstant(row.getInstant("created"), ZoneId.systemDefault()));
        }
        if (row.getInstant("modified") != null) {
            post.setModified(LocalDateTime.ofInstant(row.getInstant("modified"), ZoneId.systemDefault()));
        }
        return post;
    }
}

