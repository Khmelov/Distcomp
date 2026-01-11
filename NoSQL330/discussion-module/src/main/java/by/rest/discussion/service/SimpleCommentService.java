package by.rest.discussion.service;

import by.rest.discussion.domain.Comment;
import by.rest.discussion.dto.CommentRequestTo;
import by.rest.discussion.dto.CommentResponseTo;
import by.rest.discussion.repository.CommentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SimpleCommentService {
    
    private final CommentRepository repository;
    
    public SimpleCommentService(CommentRepository repository) {
        this.repository = repository;
    }
    
    public CommentResponseTo create(CommentRequestTo request) {
        Long storyId = request.getStoryId();
        Long nextId = 1L; // Простая генерация
        
        Comment comment = new Comment(storyId, nextId, request.getContent());
        comment = repository.save(comment);
        
        return toResponse(comment);
    }
    
    public List<CommentResponseTo> getAll() {
        return repository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
    
    private CommentResponseTo toResponse(Comment comment) {
        CommentResponseTo response = new CommentResponseTo();
        response.setId(comment.getId());
        response.setStoryId(comment.getStoryId());
        response.setContent(comment.getContent());
        return response;
    }
}