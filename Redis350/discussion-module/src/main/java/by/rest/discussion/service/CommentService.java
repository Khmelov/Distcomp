// src/main/java/by/rest/discussion/service/CommentService.java
package by.rest.discussion.service;

import by.rest.discussion.domain.Comment;
import by.rest.discussion.dto.CommentRequestTo;
import by.rest.discussion.dto.CommentResponseTo;
import by.rest.discussion.mapper.CommentMapper;
import by.rest.discussion.repository.CommentRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class CommentService {
    
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final AtomicLong idCounter = new AtomicLong(1);
    
    public CommentService(CommentRepository commentRepository, CommentMapper commentMapper) {
        this.commentRepository = commentRepository;
        this.commentMapper = commentMapper;
    }
    
    public CommentResponseTo createComment(CommentRequestTo request) {
        // Для простоты - генерируем ID
        Long id = idCounter.getAndIncrement();
        Comment comment = commentMapper.toEntity(request, id);
        
        // Сохраняем (в реальности через репозиторий)
        commentRepository.save(comment);
        
        return commentMapper.toResponse(comment);
    }
    
    public CommentResponseTo getCommentById(Long id) {
        // В реальности через репозиторий
        Object stored = commentRepository.find();
        if (stored instanceof Comment) {
            Comment comment = (Comment) stored;
            if (comment.getId().equals(id)) {
                return commentMapper.toResponse(comment);
            }
        }
        throw new RuntimeException("Comment not found with id: " + id);
    }
    
    public List<CommentResponseTo> getCommentsByStoryId(Long storyId) {
        // Для простоты возвращаем пустой список
        return new ArrayList<>();
    }
    
    public List<CommentResponseTo> getAllComments() {
        // Для простоты возвращаем пустой список
        return new ArrayList<>();
    }
    
    public void deleteComment(Long id) {
        // В реальности через репозиторий
        System.out.println("Deleting comment with id: " + id);
    }
}