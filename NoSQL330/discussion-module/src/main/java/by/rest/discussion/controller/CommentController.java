package by.rest.discussion.controller;

import by.rest.discussion.domain.Comment;
import by.rest.discussion.dto.CommentRequestTo;
import by.rest.discussion.dto.CommentResponseTo;
import by.rest.discussion.repository.CommentRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1.0/comments")
public class CommentController {
    
    private final CommentRepository repository;
    
    public CommentController(CommentRepository repository) {
        this.repository = repository;
    }
    
    @GetMapping("/test")
    public String test() {
        return "CommentController v2 is working!";
    }
    
    @GetMapping
    public List<CommentResponseTo> getAll() {
        return repository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
    
@GetMapping("/story-filtered/{storyId}")
public List<CommentResponseTo> getByStoryIdFiltered(@PathVariable("storyId") Long storyId) {
    try {
        // Альтернативный способ: получаем все и фильтруем
        List<Comment> allComments = repository.findAll();
        return allComments.stream()
                .filter(comment -> comment != null && 
                                  comment.getStoryId() != null && 
                                  comment.getStoryId().equals(storyId))
                .map(this::toResponse)
                .collect(Collectors.toList());
    } catch (Exception e) {
        e.printStackTrace();
        return List.of();
    }
}

@GetMapping("/story/{storyId}")
public List<CommentResponseTo> getByStoryId(@PathVariable("storyId") Long storyId) {
    try {
        // Используем ту же логику что и в story-filtered
        List<Comment> allComments = repository.findAll();
        
        return allComments.stream()
                .filter(comment -> comment != null && 
                                  comment.getStoryId() != null && 
                                  comment.getStoryId().equals(storyId))
                .sorted((c1, c2) -> {
                    // Сортируем по ID
                    Long id1 = c1.getId();
                    Long id2 = c2.getId();
                    if (id1 == null && id2 == null) return 0;
                    if (id1 == null) return -1;
                    if (id2 == null) return 1;
                    return id1.compareTo(id2);
                })
                .map(this::toResponse)
                .collect(Collectors.toList());
    } catch (Exception e) {
        e.printStackTrace();
        return List.of();
    }
}
    
    @PostMapping
    public CommentResponseTo create(@RequestBody CommentRequestTo request) {
        try {
            Long storyId = request.getStoryId();
            
            // Находим максимальный ID для этого storyId
            Long maxId = repository.findMaxIdByStoryId(storyId);
            Long nextId = (maxId != null) ? maxId + 1 : 1L;
            
            Comment comment = new Comment(storyId, nextId, request.getContent());
            comment = repository.save(comment);
            
            return toResponse(comment);
        } catch (Exception e) {
            e.printStackTrace();
            // Fallback: простая генерация
            Comment comment = new Comment(request.getStoryId(), System.currentTimeMillis(), request.getContent());
            comment = repository.save(comment);
            return toResponse(comment);
        }
    }
    
    @GetMapping("/story/{storyId}/{id}")
    public CommentResponseTo getById(
            @PathVariable("storyId") Long storyId,
            @PathVariable("id") Long id) {
        return repository.findByStoryIdAndId(storyId, id)
                .map(this::toResponse)
                .orElse(null);
    }
    
    @PutMapping("/story/{storyId}/{id}")
    public CommentResponseTo update(
            @PathVariable("storyId") Long storyId,
            @PathVariable("id") Long id,
            @RequestBody CommentRequestTo request) {
        
        return repository.findByStoryIdAndId(storyId, id)
                .map(comment -> {
                    comment.setContent(request.getContent());
                    comment = repository.save(comment);
                    return toResponse(comment);
                })
                .orElse(null);
    }
    
    @DeleteMapping("/story/{storyId}/{id}")
    public String delete(
            @PathVariable("storyId") Long storyId,
            @PathVariable("id") Long id) {
        try {
            repository.deleteByStoryIdAndId(storyId, id);
            return "Comment deleted successfully";
        } catch (Exception e) {
            return "Error deleting comment: " + e.getMessage();
        }
    }

    
    
    private CommentResponseTo toResponse(Comment comment) {
        if (comment == null) {
            return null;
        }
        
        CommentResponseTo response = new CommentResponseTo();
        response.setId(comment.getId());
        response.setStoryId(comment.getStoryId());
        response.setContent(comment.getContent());
        return response;
    }
}