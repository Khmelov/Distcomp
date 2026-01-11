package by.rest.publisher.service;

import by.rest.publisher.config.RedisCacheService;
import by.rest.publisher.domain.Comment;
import by.rest.publisher.dto.comment.CommentRequestTo;
import by.rest.publisher.dto.comment.CommentResponseTo;
import by.rest.publisher.dto.kafka.CommentKafkaRequest;
import by.rest.publisher.exception.ApiException;
import by.rest.publisher.mapper.CommentMapper;
import by.rest.publisher.repository.CommentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
@Slf4j
public class CommentService {
    
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final KafkaProducerService kafkaProducerService;
    private final RedisCacheService redisCacheService;
    
    public CommentService(CommentRepository commentRepository, 
                         CommentMapper commentMapper,
                         KafkaProducerService kafkaProducerService,
                         RedisCacheService redisCacheService) {
        this.commentRepository = commentRepository;
        this.commentMapper = commentMapper;
        this.kafkaProducerService = kafkaProducerService;
        this.redisCacheService = redisCacheService;
    }
    
    @Caching(evict = {
        @CacheEvict(value = "allComments", allEntries = true),
        @CacheEvict(value = "commentsByStory", key = "#request.storyId"),
        @CacheEvict(value = "commentsByStatus", allEntries = true)
    })
    public CommentResponseTo createComment(CommentRequestTo request) {
        validateRequest(request);
        
        Comment comment = commentMapper.toEntity(request);
        comment.setStatus("PENDING");
        
        comment = commentRepository.save(comment);
        log.info("Comment saved to database: id={}, storyId={}, status={}", 
                comment.getId(), comment.getStoryId(), comment.getStatus());
        
        // Отправляем на модерацию через Kafka
        CommentKafkaRequest kafkaRequest = new CommentKafkaRequest(
            comment.getId().toString(), 
            comment.getContent(), 
            comment.getStoryId(),
            "anonymous"
        );
        
        kafkaProducerService.sendCommentForModeration(kafkaRequest);
        
        CommentResponseTo response = commentMapper.toResponse(comment);
        
        // Кэшируем в Redis
        redisCacheService.cacheComment(comment.getId().toString(), response);
        log.info("Comment cached in Redis: id={}", comment.getId());
        
        return response;
    }
    
    @Cacheable(value = "comments", key = "#id", unless = "#result == null")
    @Transactional(readOnly = true)
    public CommentResponseTo getCommentById(UUID id) {
        log.info("Fetching comment from database: id={}", id);
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ApiException(404, "40405", "Comment not found with id: " + id));
        
        CommentResponseTo response = commentMapper.toResponse(comment);
        
        // Кэшируем в Redis
        redisCacheService.cacheComment(id.toString(), response);
        log.info("Comment cached in Redis: id={}", id);
        
        return response;
    }
    
    @Cacheable(value = "commentsByStory", key = "#storyId", unless = "#result.isEmpty()")
    @Transactional(readOnly = true)
    public List<CommentResponseTo> getCommentsByStoryId(Long storyId) {
        log.info("Fetching comments by story from database: storyId={}", storyId);
        List<Comment> comments = commentRepository.findByStoryId(storyId);
        
        List<CommentResponseTo> response = comments.stream()
                .map(commentMapper::toResponse)
                .toList();
        
        // Кэшируем отдельные комментарии
        comments.forEach(comment -> {
            redisCacheService.cacheComment(comment.getId().toString(), commentMapper.toResponse(comment));
        });
        
        return response;
    }
    
    @Caching(evict = {
        @CacheEvict(value = "comments", key = "#id"),
        @CacheEvict(value = "allComments", allEntries = true),
        @CacheEvict(value = "commentsByStatus", allEntries = true)
    })
    public void deleteComment(UUID id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ApiException(404, "40405", "Comment not found with id: " + id));
        
        // Удаляем из кэша Redis
        redisCacheService.deleteComment(id.toString());
        log.info("Comment deleted from Redis cache: id={}", id);
        
        commentRepository.deleteById(id);
    }
    
    @Cacheable(value = "allComments", unless = "#result.isEmpty()")
    @Transactional(readOnly = true)
    public List<CommentResponseTo> getAllComments() {
        log.info("Fetching all comments from database");
        List<CommentResponseTo> comments = commentRepository.findAll().stream()
                .map(commentMapper::toResponse)
                .toList();
        
        // Кэшируем в Redis
        redisCacheService.cacheAllComments(comments);
        log.info("All comments cached in Redis");
        
        return comments;
    }
    
    @Cacheable(value = "commentsByStatus", key = "#status", unless = "#result.isEmpty()")
    public List<CommentResponseTo> getCommentsByStatus(String status) {
        log.info("Fetching comments by status from database: status={}", status);
        List<Comment> comments = commentRepository.findByStatus(status);
        
        List<CommentResponseTo> response = comments.stream()
                .map(commentMapper::toResponse)
                .toList();
        
        return response;
    }
    
    @Caching(
        put = @CachePut(value = "comments", key = "#commentId"),
        evict = {
            @CacheEvict(value = "allComments", allEntries = true),
            @CacheEvict(value = "commentsByStatus", allEntries = true)
        }
    )
    public void updateCommentStatus(UUID commentId, String status) {
        commentRepository.findById(commentId).ifPresentOrElse(
            comment -> {
                String oldStatus = comment.getStatus();
                comment.setStatus(status);
                comment = commentRepository.save(comment);
                log.info("Comment status updated: id={}, oldStatus={}, newStatus={}", 
                        commentId, oldStatus, status);
                
                // Обновляем кэш в Redis
                CommentResponseTo response = commentMapper.toResponse(comment);
                redisCacheService.cacheComment(commentId.toString(), response);
                log.info("Comment cache updated in Redis: id={}", commentId);
            },
            () -> {
                log.warn("Cannot update status: comment not found in database, id={}", commentId);
            }
        );
    }
    
    // Метод для получения комментария с кэшированием из Redis
    @Transactional(readOnly = true)
    public CommentResponseTo getCommentByIdWithCache(UUID id) {
        // Сначала пробуем получить из Redis
        Object cachedComment = redisCacheService.getComment(id.toString());
        if (cachedComment != null) {
            log.info("Comment retrieved from Redis cache: id={}", id);
            return (CommentResponseTo) cachedComment;
        }
        
        // Если нет в кэше, получаем из БД
        return getCommentById(id);
    }
    
    private void validateRequest(CommentRequestTo request) {
        if (request.getStoryId() == null || request.getStoryId() <= 0) {
            throw new ApiException(400, "40001", "Story ID must be positive");
        }
        
        if (request.getContent() == null || request.getContent().trim().isEmpty()) {
            throw new ApiException(400, "40002", "Content cannot be empty");
        }
        
        if (request.getContent().length() < 2) {
            throw new ApiException(400, "40003", "Content must be at least 2 characters long");
        }
        
        if (request.getContent().length() > 2048) {
            throw new ApiException(400, "40004", "Content cannot exceed 2048 characters");
        }
    }
}