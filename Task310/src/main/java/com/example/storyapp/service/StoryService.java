package com.example.storyapp.service;

import com.example.storyapp.dto.StoryRequestTo;
import com.example.storyapp.dto.StoryResponseTo;
import com.example.storyapp.exception.AppException;
import com.example.storyapp.model.Story;
import com.example.storyapp.repository.InMemoryStoryRepository;
import com.example.storyapp.repository.InMemoryUserRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StoryService {
    private final InMemoryStoryRepository storyRepo;
    private final InMemoryUserRepository userRepo;

    public StoryService(InMemoryStoryRepository storyRepo, InMemoryUserRepository userRepo) {
        this.storyRepo = storyRepo;
        this.userRepo = userRepo;
    }

    public List<StoryResponseTo> getAllStories() {
        return storyRepo.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public StoryResponseTo getStoryById(@NotNull Long id) {
        return storyRepo.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new AppException("Story not found", 40402));
    }

    public StoryResponseTo createStory(@Valid StoryRequestTo request) {
        if (!userRepo.findById(request.userId()).isPresent()) {
            throw new AppException("User not found for story creation", 40405);
        }
        Story story = toEntity(request);
        story.setCreated(java.time.Instant.now());
        story.setModified(java.time.Instant.now());
        Story saved = storyRepo.save(story);
        return toResponse(saved);
    }

    public StoryResponseTo updateStory(@Valid StoryRequestTo request) {
        if (request.id() == null) {
            throw new AppException("ID required for update", 40003);
        }
        if (!storyRepo.findById(request.id()).isPresent()) {
            throw new AppException("Story not found for update", 40402);
        }
        if (!userRepo.findById(request.userId()).isPresent()) {
            throw new AppException("User not found for story update", 40405);
        }
        Story story = toEntity(request);
        story.setModified(java.time.Instant.now());
        Story updated = storyRepo.save(story);
        return toResponse(updated);
    }

    public void deleteStory(@NotNull Long id) {
        if (!storyRepo.deleteById(id)) {
            throw new AppException("Story not found for deletion", 40402);
        }
    }

    private Story toEntity(StoryRequestTo dto) {
        Story story = new Story();
        story.setId(dto.id());
        story.setUserId(dto.userId());
        story.setTitle(dto.title());
        story.setContent(dto.content());
        return story;
    }

    private StoryResponseTo toResponse(Story story) {
        return new StoryResponseTo(
                story.getId(),
                story.getUserId(),
                story.getTitle(),
                story.getContent(),
                story.getCreated(),
                story.getModified()
        );
    }
}