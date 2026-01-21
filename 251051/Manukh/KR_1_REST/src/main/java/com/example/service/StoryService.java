package com.example.service;

import com.example.dto.request.StoryRequestTo;
import com.example.dto.response.StoryResponseTo;
import com.example.exception.NotFoundException;
import com.example.model.Story;
import com.example.repository.InMemoryEditorRepository;
import com.example.repository.InMemoryMarkRepository;
import com.example.repository.InMemoryStoryRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Validated
public class StoryService {

    @Autowired
    private InMemoryStoryRepository storyRepository;

    @Autowired
    private InMemoryEditorRepository editorRepository;

    @Autowired
    private InMemoryMarkRepository markRepository;

    public List<StoryResponseTo> getAllStories() {
        return storyRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public StoryResponseTo getStoryById(Long id) {
        Story story = storyRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Story not found with id: " + id, 40402));
        return convertToResponse(story);
    }

    public StoryResponseTo createStory(@Valid StoryRequestTo request) {
        if (!editorRepository.existsById(request.getEditorId())) {
            throw new NotFoundException("Editor not found with id: " + request.getEditorId(), 40401);
        }

        Story story = convertToEntity(request);
        Story savedStory = storyRepository.save(story);
        return convertToResponse(savedStory);
    }

    public StoryResponseTo updateStory(Long id, @Valid StoryRequestTo request) {
        Story existingStory = storyRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Story not found with id: " + id, 40402));

        if (!editorRepository.existsById(request.getEditorId())) {
            throw new NotFoundException("Editor not found with id: " + request.getEditorId(), 40401);
        }

        existingStory.setEditorId(request.getEditorId());
        existingStory.setTitle(request.getTitle());
        existingStory.setContent(request.getContent());

        Story updatedStory = storyRepository.update(existingStory);
        return convertToResponse(updatedStory);
    }

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
        return stories.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public void addMarkToStory(Long storyId, Long markId) {
        Story story = storyRepository.findById(storyId)
                .orElseThrow(() -> new NotFoundException("Story not found with id: " + storyId, 40402));

        if (!markRepository.existsById(markId)) {
            throw new NotFoundException("Mark not found with id: " + markId, 40403);
        }

        story.addMark(markId);
        storyRepository.update(story);

        markRepository.findById(markId).ifPresent(mark -> {
            mark.addStory(storyId);
            markRepository.update(mark);
        });
    }

    public void removeMarkFromStory(Long storyId, Long markId) {
        Story story = storyRepository.findById(storyId)
                .orElseThrow(() -> new NotFoundException("Story not found with id: " + storyId, 40402));

        if (!markRepository.existsById(markId)) {
            throw new NotFoundException("Mark not found with id: " + markId, 40403);
        }

        story.removeMark(markId);
        storyRepository.update(story);

        markRepository.findById(markId).ifPresent(mark -> {
            mark.removeStory(storyId);
            markRepository.update(mark);
        });
    }

    private Story convertToEntity(StoryRequestTo request) {
        Story story = new Story();
        story.setEditorId(request.getEditorId());
        story.setTitle(request.getTitle());
        story.setContent(request.getContent());
        story.setCreated(LocalDateTime.now());
        story.setModified(LocalDateTime.now());
        return story;
    }

    private StoryResponseTo convertToResponse(Story story) {
        StoryResponseTo response = new StoryResponseTo();
        response.setId(story.getId());
        response.setEditorId(story.getEditorId());
        response.setTitle(story.getTitle());
        response.setContent(story.getContent());
        response.setCreated(story.getCreated());
        response.setModified(story.getModified());
        response.setMarkIds(story.getMarkIds());
        return response;
    }
}