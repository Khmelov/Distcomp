package by.rest.publisher.service;

import by.rest.publisher.domain.Comment;
import by.rest.publisher.dto.comment.CommentRequestTo;
import by.rest.publisher.dto.comment.CommentResponseTo;
import by.rest.publisher.dto.kafka.CommentKafkaRequest;
import by.rest.publisher.exception.ApiException;
import by.rest.publisher.mapper.CommentMapper;
import by.rest.publisher.repository.CommentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class CommentService {
    
    private static final Logger logger = LoggerFactory.getLogger(CommentService.class);
    
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final KafkaProducerService kafkaProducerService;
    
    public CommentService(CommentRepository commentRepository, 
                         CommentMapper commentMapper,
                         KafkaProducerService kafkaProducerService) {
        this.commentRepository = commentRepository;
        this.commentMapper = commentMapper;
        this.kafkaProducerService = kafkaProducerService;
    }
    
    public CommentResponseTo createComment(CommentRequestTo request) {
        validateRequest(request);
        
        // Создаем сущность Comment
        Comment comment = commentMapper.toEntity(request);
        comment.setStatus("PENDING");
        
        // Сохраняем в БД
        comment = commentRepository.save(comment);
        logger.info("Comment saved to database: id={}, storyId={}, status={}", 
                comment.getId(), comment.getStoryId(), comment.getStatus());
        
        // Отправляем на модерацию через Kafka
        CommentKafkaRequest kafkaRequest = new CommentKafkaRequest(
            comment.getId().toString(), 
            request.getContent(), 
            request.getStoryId(),
            request.getAuthor() != null ? request.getAuthor() : "anonymous",
            System.currentTimeMillis()
        );
        
        kafkaProducerService.sendCommentForModeration(kafkaRequest);
        
        logger.info("Comment sent to Kafka for moderation: id={}", comment.getId());
        
        // Возвращаем DTO
        return commentMapper.toResponse(comment);
    }
    
    @Transactional(readOnly = true)
    public CommentResponseTo getCommentById(UUID id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ApiException(404, "40401", "Comment not found with id: " + id));
        
        return commentMapper.toResponse(comment);
    }
    
    @Transactional(readOnly = true)
    public List<CommentResponseTo> getCommentsByStoryId(Long storyId) {
        List<Comment> comments = commentRepository.findByStoryId(storyId);
        return comments.stream()
                .map(commentMapper::toResponse)
                .toList();
    }
    
    public void updateCommentStatus(UUID commentId, String status) {
        commentRepository.findById(commentId).ifPresentOrElse(
            comment -> {
                String oldStatus = comment.getStatus();
                comment.setStatus(status);
                commentRepository.save(comment);
                logger.info("Comment status updated: id={}, oldStatus={}, newStatus={}", 
                        commentId, oldStatus, status);
            },
            () -> {
                logger.warn("Cannot update status: comment not found in database, id={}", commentId);
            }
        );
    }
    
    public void deleteComment(UUID id) {
        if (!commentRepository.existsById(id)) {
            throw new ApiException(404, "40401", "Comment not found with id: " + id);
        }
        commentRepository.deleteById(id);
        logger.info("Comment deleted from database: id={}", id);
    }
    
    @Transactional(readOnly = true)
    public List<CommentResponseTo> getAllComments() {
        return commentRepository.findAll().stream()
                .map(commentMapper::toResponse)
                .toList();
    }
    
    public List<CommentResponseTo> getCommentsByStatus(String status) {
        List<Comment> comments = commentRepository.findByStatus(status);
        return comments.stream()
                .map(commentMapper::toResponse)
                .toList();
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
    
    public long getCommentCount() {
        return commentRepository.count();
    }
    
    public long getCommentCountByStory(Long storyId) {
        return commentRepository.countByStoryId(storyId);
    }
} 