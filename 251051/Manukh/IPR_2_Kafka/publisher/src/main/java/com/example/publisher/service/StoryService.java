package com.example.publisher.service;

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
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private StoryRepository storyRepository;

    @Autowired
    private EditorRepository editorRepository;

    @Autowired
    private MarkRepository markRepository;

    @Autowired
    private StoryMapper storyMapper;

    public List<StoryResponseTo> getAllStories() {
        return storyMapper.toResponseList(storyRepository.findAll());
    }

    public Page<StoryResponseTo> getAllStories(Pageable pageable) {
        return storyRepository.findAll(pageable)
                .map(storyMapper::toResponse);
    }

    public StoryResponseTo getStoryById(Long id) {
        Story story = storyRepository.findByIdWithAllRelations(id)
                .orElseThrow(() -> new NotFoundException("Story not found with id: " + id, 40402));
        return storyMapper.toResponse(story);
    }

    @Transactional
    public StoryResponseTo createStory(@Valid StoryRequestTo request) {
        Editor editor = editorRepository.findById(request.getEditorId())
                .orElseThrow(() -> new NotFoundException("Editor not found with id: " + request.getEditorId(), 40401));

        Story story = storyMapper.toEntity(request);
        story.setEditor(editor);
        story.setCreatedAt(LocalDateTime.now());
        story.setModifiedAt(LocalDateTime.now());

        Story savedStory = storyRepository.save(story);
        return storyMapper.toResponse(savedStory);
    }

    @Transactional
    public StoryResponseTo updateStory(Long id, @Valid StoryRequestTo request) {
        Story existingStory = storyRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Story not found with id: " + id, 40402));

        if (!editorRepository.existsById(request.getEditorId())) {
            throw new NotFoundException("Editor not found with id: " + request.getEditorId(), 40401);
        }

        Editor editor = editorRepository.findById(request.getEditorId())
                .orElseThrow(() -> new NotFoundException("Editor not found with id: " + request.getEditorId(), 40401));

        storyMapper.updateEntity(request, existingStory);
        existingStory.setEditor(editor);
        existingStory.setModifiedAt(LocalDateTime.now());

        Story updatedStory = storyRepository.save(existingStory);
        return storyMapper.toResponse(updatedStory);
    }

    @Transactional
    public void deleteStory(Long id) {
        if (!storyRepository.existsById(id)) {
            throw new NotFoundException("Story not found with id: " + id, 40402);
        }
        storyRepository.deleteById(id);
    }

    public List<StoryResponseTo> getStoriesByEditorId(Long editorId) {
        if (!editorRepository.existsById(editorId)) {
            throw new NotFoundException("Editor not found with id: " + editorId, 40401);
        }

        List<Story> stories = storyRepository.findByEditorId(editorId);
        return storyMapper.toResponseList(stories);
    }

    public Page<StoryResponseTo> getStoriesByEditorId(Long editorId, Pageable pageable) {
        if (!editorRepository.existsById(editorId)) {
            throw new NotFoundException("Editor not found with id: " + editorId, 40401);
        }

        return storyRepository.findByEditorId(editorId, pageable)
                .map(storyMapper::toResponse);
    }

    @Transactional
    public void addMarkToStory(Long storyId, Long markId) {
        Story story = storyRepository.findById(storyId)
                .orElseThrow(() -> new NotFoundException("Story not found with id: " + storyId, 40402));

        Mark mark = markRepository.findById(markId)
                .orElseThrow(() -> new NotFoundException("Mark not found with id: " + markId, 40403));

        if (!story.getMarks().contains(mark)) {
            story.addMark(mark);
            storyRepository.save(story);
        }
    }

    @Transactional
    public void removeMarkFromStory(Long storyId, Long markId) {
        Story story = storyRepository.findById(storyId)
                .orElseThrow(() -> new NotFoundException("Story not found with id: " + storyId, 40402));

        Mark mark = markRepository.findById(markId)
                .orElseThrow(() -> new NotFoundException("Mark not found with id: " + markId, 40403));

        if (story.getMarks().contains(mark)) {
            story.removeMark(mark);
            storyRepository.save(story);
        }
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