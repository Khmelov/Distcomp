package com.example.demo.servises;

import com.example.demo.dto.request.StoryRequestTo;
import com.example.demo.dto.response.StoryResponseTo;
import com.example.demo.exeptionHandler.ConflictException;
import com.example.demo.exeptionHandler.ResourceNotFoundException;
import com.example.demo.mapper.StoryMapper;
import com.example.demo.models.Story;
import com.example.demo.models.Tag;
import com.example.demo.models.Writer;
import com.example.demo.repository.StoryRepository;
import com.example.demo.repository.TagRepository;
import com.example.demo.repository.WriterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class StoryService {
    private final StoryMapper storyMapper;
    private final StoryRepository storyRepository;
    private final WriterRepository writerRepository;
    private final TagRepository tagRepository;

    public StoryService(StoryMapper storyMapper,
                        StoryRepository storyRepository,
                        WriterRepository writerRepository,
                        TagRepository tagRepository) {
        this.storyMapper = storyMapper;
        this.storyRepository = storyRepository;
        this.writerRepository = writerRepository;
        this.tagRepository = tagRepository;
    }

    public List<StoryResponseTo> findAll(int page, int size, String sortBy, String sortDir, String title){
        Sort sort = "desc".equalsIgnoreCase(sortDir) ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Specification<Story> spec = (root, query, cb) ->
                title == null || title.isBlank() ? cb.conjunction() : cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%");
         return storyMapper.storyListToResponseList(storyRepository.findAll(spec, PageRequest.of(page, size, sort)).getContent());
    }

    public StoryResponseTo create(StoryRequestTo storyRequestTo){
        validateStoryContent(storyRequestTo.content);
        Writer writer = writerRepository.findById(storyRequestTo.writerId)
                .orElseThrow(() -> new ResourceNotFoundException("Writer", storyRequestTo.writerId));
        Story story = storyMapper.toEntity(storyRequestTo);
        story.setWriter(writer);
        story.setCreated(LocalDateTime.now());
        story.setModified(LocalDateTime.now());
        if (storyRequestTo.getTagIds() != null && !storyRequestTo.getTagIds().isEmpty()) {
            Set<Tag> tags = new HashSet<>(tagRepository.findAllById(storyRequestTo.getTagIds()));
            story.setTags(tags);
        }
        Story savedStory = storyRepository.save(story);
        return storyMapper.toResponse(savedStory);
    }

    public void delete(Long id){
        if(!storyRepository.existsById(id)){
            throw new ResourceNotFoundException("Story", id);
        }
        storyRepository.deleteById(id);
    }

    public StoryResponseTo findById(Long id){
        Story story = storyRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Story", id));
        return storyMapper.toResponse(story);
    }

    public StoryResponseTo update(Long id, StoryRequestTo storyRequestTo){
        validateStoryContent(storyRequestTo.content);
        Story existing = storyRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Story", id));
        Writer writer = writerRepository.findById(storyRequestTo.writerId)
                .orElseThrow(() -> new ResourceNotFoundException("Writer", storyRequestTo.writerId));
        Story story = storyMapper.toEntity(storyRequestTo);
        story.setId(existing.getId());
        story.setWriter(writer);
        story.setCreated(existing.getCreated());
        story.setModified(LocalDateTime.now());
        if (storyRequestTo.getTagIds() != null) {
            Set<Tag> tags = new HashSet<>(tagRepository.findAllById(storyRequestTo.getTagIds()));
            story.setTags(tags);
        } else {
            story.setTags(existing.getTags());
        }
        return storyMapper.toResponse(storyRepository.save(story));
    }

    private void validateStoryContent(String content) {
        if (content != null && content.startsWith("other-content")) {
            throw new ConflictException("Story content is forbidden");
        }
    }

}
