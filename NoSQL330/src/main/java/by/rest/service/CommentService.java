package by.rest.service;

import by.rest.domain.Comment;
import by.rest.domain.Story;
import by.rest.dto.CommentRequestTo;
import by.rest.dto.CommentResponseTo;
import by.rest.exception.ApiException;
import by.rest.mapper.CommentMapper;
import by.rest.repository.CommentRepository;
import by.rest.repository.StoryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CommentService {
    private final CommentRepository repo;
    private final StoryRepository storyRepo;
    private final CommentMapper mapper;

    public CommentService(CommentRepository repo, StoryRepository storyRepo, CommentMapper mapper) {
        this.repo = repo;
        this.storyRepo = storyRepo;
        this.mapper = mapper;
    }

    public CommentResponseTo create(CommentRequestTo req) {
        validate(req);
        
        Story story = storyRepo.findById(req.getStoryId())
                .orElseThrow(() -> new ApiException(404, "40401", "Story not found"));
        
        Comment comment = mapper.toEntity(req);
        comment.setStory(story);
        comment = repo.save(comment);
        
        return mapper.toResponse(comment);
    }

    @Transactional(readOnly = true)
    public Page<CommentResponseTo> getAll(int page, int size) {
        return repo.findAll(PageRequest.of(page, size))
                .map(mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public CommentResponseTo getById(Long id) {
        Comment comment = repo.findById(id)
                .orElseThrow(() -> new ApiException(404, "40401", "Comment not found"));
        return mapper.toResponse(comment);
    }

    public CommentResponseTo update(Long id, CommentRequestTo req) {
        validate(req);
        
        Comment comment = repo.findById(id)
                .orElseThrow(() -> new ApiException(404, "40401", "Comment not found"));
        
        Story story = storyRepo.findById(req.getStoryId())
                .orElseThrow(() -> new ApiException(404, "40401", "Story not found"));
        
        comment.setStory(story);
        comment.setContent(req.getContent());
        comment = repo.save(comment);
        
        return mapper.toResponse(comment);
    }

    public void delete(Long id) {
        if (!repo.existsById(id)) {
            throw new ApiException(404, "40401", "Comment not found");
        }
        repo.deleteById(id);
    }

    private void validate(CommentRequestTo req) {
        if (req.getStoryId() == null) {
            throw new ApiException(400, "40002", "Story ID is required");
        }
        if (req.getContent() == null || req.getContent().length() < 2 || req.getContent().length() > 2048) {
            throw new ApiException(400, "40002", "Content must be between 2 and 2048 characters");
        }
    }
}