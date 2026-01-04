package com.example.publisher.service;

import com.example.publisher.dto.StoryRequestTo;
import com.example.publisher.dto.StoryResponseTo;
import com.example.publisher.exception.AppException;
import com.example.publisher.model.Label;
import com.example.publisher.model.Story;
import com.example.publisher.model.User;
import com.example.publisher.repository.LabelRepository;
import com.example.publisher.repository.StoryRepository;
import com.example.publisher.repository.UserRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@Transactional
public class StoryService {
    private final StoryRepository storyRepo;
    private final UserRepository userRepo;
    private final LabelRepository labelRepo;

    public StoryService(StoryRepository storyRepo, UserRepository userRepo, LabelRepository labelRepo) {
        this.storyRepo = storyRepo;
        this.userRepo = userRepo;
        this.labelRepo = labelRepo;
    }

    @Transactional(readOnly = true)
    public List<StoryResponseTo> getAllStories() {
        return storyRepo.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public StoryResponseTo getStoryById(@NotNull Long id) {
        return storyRepo.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new AppException("Story not found", 40402));
    }

    public StoryResponseTo createStory(@Valid StoryRequestTo request) {
        User user = userRepo.findById(request.userId())
                .orElseThrow(() -> new AppException("User not found", 40405));

        if (storyRepo.existsByUserIdAndTitle(request.userId(), request.title())) {
            throw new AppException("Story title must be unique per user", 40303);
        }

        Story story = new Story(user, request.title(), request.content());
        story.setCreated(Instant.now());
        story.setModified(Instant.now());

        // === Обработка labels ===
        if (request.labels() != null) {
            for (String labelName : request.labels()) {
                Label label = labelRepo.findByName(labelName)
                        .orElseGet(() -> labelRepo.save(new Label(labelName)));
                story.getLabels().add(label);
            }
        }

        Story saved = storyRepo.save(story);
        return toResponse(saved);
    }

    public StoryResponseTo updateStory(@Valid StoryRequestTo request) {
        if (request.id() == null) {
            throw new AppException("ID required for update", 40003);
        }

        Story existing = storyRepo.findById(request.id())
                .orElseThrow(() -> new AppException("Story not found for update", 40402));

        User user = userRepo.findById(request.userId())
                .orElseThrow(() -> new AppException("User not found", 40405));

        // Проверка уникальности (кроме текущей)
        if (!(existing.getUser().getId().equals(request.userId()) &&
                existing.getTitle().equals(request.title())) &&
                storyRepo.existsByUserIdAndTitle(request.userId(), request.title())) {
            throw new AppException("Story title must be unique per user", 40303);
        }

        existing.setUser(user);
        existing.setTitle(request.title());
        existing.setContent(request.content());
        existing.setModified(Instant.now());

        // === Обновление labels ===
        existing.getLabels().clear();
        if (request.labels() != null) {
            for (String labelName : request.labels()) {
                Label label = labelRepo.findByName(labelName)
                        .orElseGet(() -> labelRepo.save(new Label(labelName)));
                existing.getLabels().add(label);
            }
        }

        Story updated = storyRepo.save(existing);
        return toResponse(updated);
    }

    public void deleteStory(@NotNull Long id) {
        if (!storyRepo.existsById(id)) {
            throw new AppException("Story not found for deletion", 40402);
        }
        storyRepo.deleteById(id);
    }

    private StoryResponseTo toResponse(Story story) {
        List<String> labelNames = story.getLabels().stream()
                .map(Label::getName)
                .sorted()
                .toList();
        return new StoryResponseTo(
                story.getId(),
                story.getUser().getId(),
                story.getTitle(),
                story.getContent(),
                story.getCreated(),
                story.getModified(),
                labelNames
        );
    }
}