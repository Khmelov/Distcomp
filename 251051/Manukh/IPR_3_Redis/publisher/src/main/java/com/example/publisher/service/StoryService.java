package com.example.publisher.service;

import com.example.publisher.config.CacheConfig;
import com.example.publisher.dto.request.StoryRequestTo;
import com.example.publisher.dto.response.StoryResponseTo;
import com.example.publisher.entity.Editor;
import com.example.publisher.entity.Mark;
import com.example.publisher.entity.Story;
import com.example.publisher.exception.NotFoundException;
import com.example.publisher.mapper.StoryMapper;
import com.example.publisher.repository.EditorRepository;
import com.example.publisher.repository.MarkRepository;
import com.example.publisher.repository.StoryRepository;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Validated
@Transactional(readOnly = true)
public class StoryService {

    private static final Logger logger = LoggerFactory.getLogger(StoryService.class);

    @Autowired
    private StoryRepository storyRepository;

    @Autowired
    private EditorRepository editorRepository;

    @Autowired
    private MarkRepository markRepository;

    @Autowired
    private StoryMapper storyMapper;

    @Cacheable(value = CacheConfig.CacheNames.STORIES, key = "'all'")
    public List<StoryResponseTo> getAllStories() {
        logger.info("Fetching all stories from database");
        return storyMapper.toResponseList(storyRepository.findAll());
    }

    @Cacheable(value = CacheConfig.CacheNames.STORY_BY_ID, key = "#id")
    public StoryResponseTo getStoryById(Long id) {
        logger.info("Fetching story with id {} from database", id);
        Story story = storyRepository.findByIdWithAllRelations(id)
                .orElseThrow(() -> new NotFoundException("Story not found with id: " + id, 40402));
        return storyMapper.toResponse(story);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = CacheConfig.CacheNames.STORIES, allEntries = true),
            @CacheEvict(value = CacheConfig.CacheNames.STORY_BY_ID, key = "#result.id"),
            @CacheEvict(value = CacheConfig.CacheNames.STORIES_BY_EDITOR, key = "#request.editorId")
    })
    public StoryResponseTo createStory(@Valid StoryRequestTo request) {
        logger.info("Creating new story with title: {}", request.getTitle());

        Editor editor = editorRepository.findById(request.getEditorId())
                .orElseThrow(() -> new NotFoundException("Editor not found with id: " + request.getEditorId(), 40401));

        Story story = storyMapper.toEntity(request);
        story.setEditor(editor);
        story.setCreatedAt(LocalDateTime.now());
        story.setModifiedAt(LocalDateTime.now());

        Story savedStory = storyRepository.save(story);
        logger.info("Story created with id: {}", savedStory.getId());

        return storyMapper.toResponse(savedStory);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = CacheConfig.CacheNames.STORIES, allEntries = true),
            @CacheEvict(value = CacheConfig.CacheNames.STORY_BY_ID, key = "#id"),
            @CacheEvict(value = CacheConfig.CacheNames.STORIES_BY_EDITOR, key = "#request.editorId")
    })
    public StoryResponseTo updateStory(Long id, @Valid StoryRequestTo request) {
        logger.info("Updating story with id: {}", id);

        Story existingStory = storyRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Story not found with id: " + id, 40402));

        Editor editor = editorRepository.findById(request.getEditorId())
                .orElseThrow(() -> new NotFoundException("Editor not found with id: " + request.getEditorId(), 40401));

        storyMapper.updateEntity(request, existingStory);
        existingStory.setEditor(editor);
        existingStory.setModifiedAt(LocalDateTime.now());

        Story updatedStory = storyRepository.save(existingStory);
        logger.info("Story with id {} updated", id);

        return storyMapper.toResponse(updatedStory);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = CacheConfig.CacheNames.STORIES, allEntries = true),
            @CacheEvict(value = CacheConfig.CacheNames.STORY_BY_ID, key = "#id")
    })
    public void deleteStory(Long id) {
        logger.info("Deleting story with id: {}", id);

        if (!storyRepository.existsById(id)) {
            throw new NotFoundException("Story not found with id: " + id, 40402);
        }
        storyRepository.deleteById(id);
        logger.info("Story with id {} deleted", id);
    }

    @Cacheable(value = CacheConfig.CacheNames.STORIES_BY_EDITOR, key = "#editorId")
    public List<StoryResponseTo> getStoriesByEditorId(Long editorId) {
        logger.info("Fetching stories for editor id {} from database", editorId);

        if (!editorRepository.existsById(editorId)) {
            throw new NotFoundException("Editor not found with id: " + editorId, 40401);
        }

        List<Story> stories = storyRepository.findByEditorId(editorId);
        return storyMapper.toResponseList(stories);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = CacheConfig.CacheNames.STORY_BY_ID, key = "#storyId"),
            @CacheEvict(value = CacheConfig.CacheNames.MARKS_BY_STORY, key = "#storyId")
    })
    public void addMarkToStory(Long storyId, Long markId) {
        logger.info("Adding mark {} to story {}", markId, storyId);

        Story story = storyRepository.findById(storyId)
                .orElseThrow(() -> new NotFoundException("Story not found with id: " + storyId, 40402));

        Mark mark = markRepository.findById(markId)
                .orElseThrow(() -> new NotFoundException("Mark not found with id: " + markId, 40403));

        if (!story.getMarks().contains(mark)) {
            story.addMark(mark);
            storyRepository.save(story);
            logger.info("Mark {} added to story {}", markId, storyId);
        }
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = CacheConfig.CacheNames.STORY_BY_ID, key = "#storyId"),
            @CacheEvict(value = CacheConfig.CacheNames.MARKS_BY_STORY, key = "#storyId")
    })
    public void removeMarkFromStory(Long storyId, Long markId) {
        logger.info("Removing mark {} from story {}", markId, storyId);

        Story story = storyRepository.findById(storyId)
                .orElseThrow(() -> new NotFoundException("Story not found with id: " + storyId, 40402));

        Mark mark = markRepository.findById(markId)
                .orElseThrow(() -> new NotFoundException("Mark not found with id: " + markId, 40403));

        if (story.getMarks().contains(mark)) {
            story.removeMark(mark);
            storyRepository.save(story);
            logger.info("Mark {} removed from story {}", markId, storyId);
        }
    }

    public Page<StoryResponseTo> getAllStories(Pageable pageable) {
        return storyRepository.findAll(pageable)
                .map(storyMapper::toResponse);
    }

    public Page<StoryResponseTo> getStoriesByEditorId(Long editorId, Pageable pageable) {
        if (!editorRepository.existsById(editorId)) {
            throw new NotFoundException("Editor not found with id: " + editorId, 40401);
        }

        return storyRepository.findByEditorId(editorId, pageable)
                .map(storyMapper::toResponse);
    }

    public Page<StoryResponseTo> searchStories(String title, String content, Long editorId, Pageable pageable) {
        if (editorId != null) {
            return storyRepository.findByEditorIdAndTitleContaining(editorId, title != null ? title : "", pageable)
                    .map(storyMapper::toResponse);
        } else {
            if (title != null) {
                return storyRepository.findByTitleContainingIgnoreCase(title, pageable)
                        .map(storyMapper::toResponse);
            } else if (content != null) {
                return storyRepository.findByContentContainingIgnoreCase(content, pageable)
                        .map(storyMapper::toResponse);
            } else {
                return storyRepository.findAll(pageable)
                        .map(storyMapper::toResponse);
            }
        }
    }
}