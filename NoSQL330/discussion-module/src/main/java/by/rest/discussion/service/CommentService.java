package by.rest.discussion.service;

import by.rest.discussion.domain.Comment;
import by.rest.discussion.dto.CommentRequestTo;
import by.rest.discussion.dto.CommentResponseTo;
import by.rest.discussion.exception.ApiException;
import by.rest.discussion.mapper.CommentMapper;
import by.rest.discussion.repository.CommentRepository;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CommentService {
    
    private final CommentRepository repository;
    private final CommentMapper mapper;
    
    public CommentService(CommentRepository repository, CommentMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }
    
    public CommentResponseTo create(CommentRequestTo request) {
        validate(request);
        
        Long storyId = request.getStoryId();
        Long nextId = generateNextId(storyId);
        
        Comment comment = new Comment(storyId, nextId, request.getContent());
        comment = repository.save(comment);
        
        return mapper.toResponse(comment);
    }
    
    // Временный упрощенный метод
    @Transactional(readOnly = true)
    public List<CommentResponseTo> getAllAsList() {
        try {
            return repository.findAll()
                    .stream()
                    .map(mapper::toResponse)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }
    
    // Временный упрощенный метод для storyId
    @Transactional(readOnly = true)
    public List<CommentResponseTo> getByStoryIdAsList(Long storyId) {
        try {
            return repository.findByStoryId(storyId)
                    .stream()
                    .map(mapper::toResponse)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }
    
    @Transactional(readOnly = true)
    public Page<CommentResponseTo> getAll(int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Slice<Comment> commentsSlice = repository.findAll(pageable);
            
            List<CommentResponseTo> responses = commentsSlice.getContent()
                    .stream()
                    .map(mapper::toResponse)
                    .collect(Collectors.toList());
            
            long total = repository.count();
            return new PageImpl<>(responses, pageable, total);
        } catch (Exception e) {
            e.printStackTrace();
            return Page.empty();
        }
    }
    
    @Transactional(readOnly = true)
    public Page<CommentResponseTo> getByStoryId(Long storyId, int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Slice<Comment> commentsSlice = repository.findByStoryId(storyId, pageable);
            
            List<CommentResponseTo> responses = commentsSlice.getContent()
                    .stream()
                    .map(mapper::toResponse)
                    .collect(Collectors.toList());
            
            long total = repository.countByStoryId(storyId);
            return new PageImpl<>(responses, pageable, total);
        } catch (Exception e) {
            e.printStackTrace();
            return Page.empty();
        }
    }
    
    @Transactional(readOnly = true)
    public CommentResponseTo getById(Long storyId, Long commentId) {
        Comment comment = repository.findByStoryIdAndId(storyId, commentId)
                .orElseThrow(() -> new ApiException(404, "40401", 
                    String.format("Comment not found with storyId=%d, id=%d", storyId, commentId)));
        
        return mapper.toResponse(comment);
    }
    
    public CommentResponseTo update(Long storyId, Long commentId, CommentRequestTo request) {
        validate(request);
        
        Comment comment = repository.findByStoryIdAndId(storyId, commentId)
                .orElseThrow(() -> new ApiException(404, "40401", 
                    String.format("Comment not found with storyId=%d, id=%d", storyId, commentId)));
        
        comment.setContent(request.getContent());
        comment = repository.save(comment);
        
        return mapper.toResponse(comment);
    }
    
    public void delete(Long storyId, Long commentId) {
        Comment.CommentKey key = new Comment.CommentKey(storyId, commentId);
        if (!repository.existsById(key)) {
            throw new ApiException(404, "40401", 
                String.format("Comment not found with storyId=%d, id=%d", storyId, commentId));
        }
        
        repository.deleteByStoryIdAndId(storyId, commentId);
    }
    
    private void validate(CommentRequestTo request) {
        if (request.getStoryId() == null || request.getStoryId() <= 0) {
            throw new ApiException(400, "40002", "Story ID must be positive");
        }
        
        if (request.getContent() == null || request.getContent().trim().isEmpty()) {
            throw new ApiException(400, "40002", "Content is required");
        }
        
        if (request.getContent().length() < 2 || request.getContent().length() > 2048) {
            throw new ApiException(400, "40002", "Content must be between 2 and 2048 characters");
        }
    }
    
    private Long generateNextId(Long storyId) {
        Long maxId = repository.findMaxIdByStoryId(storyId);
        return maxId != null ? maxId + 1 : 1L;
    }
}