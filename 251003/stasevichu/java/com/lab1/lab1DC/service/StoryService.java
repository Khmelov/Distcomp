package com.lab1.lab1DC.service;

import com.lab1.lab1DC.dto.StoryRequestTo;
import com.lab1.lab1DC.dto.StoryResponseTo;
import com.lab1.lab1DC.entity.Story;
import com.lab1.lab1DC.repository.StoryRepositoryJPA;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StoryService {
    private final StoryRepositoryJPA storyRepositoryJPA;
    public List<StoryResponseTo> getAllStory() {
        return storyRepositoryJPA.findAll().stream()
                .map(story -> new StoryResponseTo(story.getId(), story.getTitle(), story.getContent(), story.getUserId(), story.getStickersIds(), story.getNoticeIds()))
                .collect(Collectors.toList());
    }

    public StoryResponseTo getStoryById(Long id) {
        return storyRepositoryJPA.findById(id)
                .map(story -> new StoryResponseTo(story.getId(), story.getTitle(), story.getContent(), story.getUserId(), story.getStickersIds(), story.getNoticeIds()))
                .orElseThrow(() -> new NoSuchElementException("Story not found"));
    }

    public StoryResponseTo createStory(Story story) {
        storyRepositoryJPA.save(story);
        return new StoryResponseTo(story.getId(), story.getTitle(), story.getContent(), story.getUserId(), story.getStickersIds(), story.getNoticeIds());
    }

    public StoryResponseTo updateStory(Long id, StoryRequestTo request) {
        Story story = storyRepositoryJPA.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Story not found"));

        if (request.getTitle() == null || request.getTitle().length() < 3) {
            throw new IllegalArgumentException("Title must be at least 3 characters long");
        }

        story.setTitle(request.getTitle());
        story.setContent(request.getContent());
        story.setUserId(request.getUserId());
        story.setStickersIds(request.getStickerIds());
        storyRepositoryJPA.save(story);
        return new StoryResponseTo(id, story.getTitle(), story.getContent(), story.getUserId(), story.getStickersIds(), story.getNoticeIds());
    }

    public void deleteStory(Long id) {
        Story story = storyRepositoryJPA.findById(id).orElseThrow(() -> new NoSuchElementException("Story no found"));
        storyRepositoryJPA.deleteById(id);
    }
}
