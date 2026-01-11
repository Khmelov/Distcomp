package by.rest.service;

import by.rest.domain.Editor;
import by.rest.domain.Story;
import by.rest.domain.Tag;
import by.rest.dto.StoryRequestTo;
import by.rest.dto.StoryResponseTo;
import by.rest.exception.ApiException;
import by.rest.mapper.StoryMapper;
import by.rest.repository.EditorRepository;
import by.rest.repository.StoryRepository;
import by.rest.repository.TagRepository;
import by.rest.spec.StorySpecs;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Service
@Transactional
public class StoryService {
    private final StoryRepository repo;
    private final EditorRepository editorRepo;
    private final TagRepository tagRepo;
    private final StoryMapper mapper;

    public StoryService(StoryRepository repo, EditorRepository editorRepo, 
                       TagRepository tagRepo, StoryMapper mapper) {
        this.repo = repo;
        this.editorRepo = editorRepo;
        this.tagRepo = tagRepo;
        this.mapper = mapper;
    }

    public StoryResponseTo create(StoryRequestTo req) {
        validate(req);
        
        Editor editor = editorRepo.findById(req.getEditorId())
                .orElseThrow(() -> new ApiException(404, "40401", "Editor not found"));
        
        Story story = mapper.toEntity(req);
        story.setEditor(editor);
        story.setCreated(Instant.now());
        story.setModified(Instant.now());
        
        if (req.getTagIds() != null && !req.getTagIds().isEmpty()) {
            Set<Tag> tags = new HashSet<>(tagRepo.findAllById(req.getTagIds()));
            if (tags.size() != req.getTagIds().size()) {
                throw new ApiException(400, "40002", "Some tags not found");
            }
            story.setTags(tags);
        }
        
        story = repo.save(story);
        return mapper.toResponse(story);
    }

    @Transactional(readOnly = true)
    public Page<StoryResponseTo> getAll(int page, int size, String sort) {
        Pageable pageable = toPageable(page, size, sort);
        return repo.findAll(pageable)
                .map(mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public StoryResponseTo getById(Long id) {
        Story story = repo.findById(id)
                .orElseThrow(() -> new ApiException(404, "40401", "Story not found"));
        return mapper.toResponse(story);
    }

    public StoryResponseTo update(Long id, StoryRequestTo req) {
        validate(req);
        
        Story story = repo.findById(id)
                .orElseThrow(() -> new ApiException(404, "40401", "Story not found"));
        
        Editor editor = editorRepo.findById(req.getEditorId())
                .orElseThrow(() -> new ApiException(404, "40401", "Editor not found"));
        
        story.setEditor(editor);
        story.setTitle(req.getTitle());
        story.setContent(req.getContent());
        story.setModified(Instant.now());
        
        if (req.getTagIds() != null) {
            Set<Tag> tags = new HashSet<>(tagRepo.findAllById(req.getTagIds()));
            if (tags.size() != req.getTagIds().size()) {
                throw new ApiException(400, "40002", "Some tags not found");
            }
            story.setTags(tags);
        }
        
        story = repo.save(story);
        return mapper.toResponse(story);
    }

    public void delete(Long id) {
        if (!repo.existsById(id)) {
            throw new ApiException(404, "40401", "Story not found");
        }
        repo.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Page<StoryResponseTo> search(String editorLogin, Long tagId, String title, 
                                       String content, int page, int size, String sort) {
        Specification<Story> spec = Specification.where(StorySpecs.hasEditorLogin(editorLogin))
                .and(StorySpecs.hasTagId(tagId))
                .and(StorySpecs.titleContains(title))
                .and(StorySpecs.contentContains(content));
        
        Pageable pageable = toPageable(page, size, sort);
        return repo.findAll(spec, pageable)
                .map(mapper::toResponse);
    }

    private Pageable toPageable(int page, int size, String sort) {
        String[] parts = sort.split(",", 2);
        String property = parts[0];
        String direction = parts.length > 1 ? parts[1] : "asc";
        
        Sort sortObj = "desc".equalsIgnoreCase(direction) 
                ? Sort.by(property).descending() 
                : Sort.by(property).ascending();
        
        return PageRequest.of(Math.max(page, 0), Math.max(size, 1), sortObj);
    }

    private void validate(StoryRequestTo req) {
        if (req.getEditorId() == null) {
            throw new ApiException(400, "40002", "Editor ID is required");
        }
        if (req.getTitle() == null || req.getTitle().length() < 2 || req.getTitle().length() > 64) {
            throw new ApiException(400, "40002", "Title must be between 2 and 64 characters");
        }
        if (req.getContent() == null || req.getContent().length() < 4 || req.getContent().length() > 2048) {
            throw new ApiException(400, "40002", "Content must be between 4 and 2048 characters");
        }
    }
}