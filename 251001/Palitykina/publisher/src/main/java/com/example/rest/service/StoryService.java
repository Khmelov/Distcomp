package com.example.rest.service;

import com.example.rest.dto.requestDto.StoryRequestTo;
import com.example.rest.dto.responseDto.StoryResponseTo;
import com.example.rest.dto.updateDto.StoryUpdateTo;
import com.example.rest.entity.Label;
import com.example.rest.entity.Story;
import com.example.rest.entity.User;
import com.example.rest.exeption.StoryNotFoundException;
import com.example.rest.exeption.UniqueConstraintViolationException;
import com.example.rest.exeption.UserNotFoundException;
import com.example.rest.mapper.StoryMapper;
import com.example.rest.repository.LabelRepo;
import com.example.rest.repository.StoryRepo;
import com.example.rest.repository.UserRepo;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@AllArgsConstructor
public class StoryService {
    private final StoryRepo storyRepo;
    private final UserRepo userRepo;
    private final StoryMapper storyMapper;
    private final LabelRepo labelRepo;

    public List<StoryResponseTo> getAll() {
        return storyRepo
                .getAll()
                .map(storyMapper::ToStoryResponseTo)
                .toList();
    }
    public StoryResponseTo get(long id) {
        return storyRepo
                .get(id)
                .map(storyMapper::ToStoryResponseTo)
                .orElse(null);
    }

    public StoryResponseTo create(StoryRequestTo input) {
        // Преобразуем входящий объект в сущность Story
        Story story = storyMapper.ToStory(input);
        story.setCreated(OffsetDateTime.now());
        story.setModified(story.getCreated());

        User user = userRepo.findById(input.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found with id " + input.getUserId()));
        story.setUser(user);

        if (input.getLabels() != null && !input.getLabels().isEmpty()) {
            story.getLabels().addAll(findOrCreateLabels(input.getLabels()));
        }


        // Сохраняем сущность Story в репозитории
        try {
            return storyRepo
                    .create(story)
                    .map(storyMapper::ToStoryResponseTo)
                    .orElseThrow();
        } catch (DataIntegrityViolationException e) {
            throw new UniqueConstraintViolationException("Title is already taken");
        }
    }

    private Set<Label> findOrCreateLabels(List<String> labelNames) {
        Set<Label> existingLabels = new HashSet<>(labelRepo.findByNameIn(labelNames));
        for (String name : labelNames) {
            if (existingLabels.stream().noneMatch(label -> label.getName().equals(name))) {
                Label label = new Label();
                label.setName(name);
                labelRepo.create(label).orElseThrow();
                existingLabels.add(labelRepo.create(label).orElseThrow());
            }
        }
        return existingLabels;
    }


    public StoryResponseTo update(StoryUpdateTo input) {
        try {
            // Преобразуем входящий объект в сущность Story
            Story story = storyMapper.ToStory(input);

            // Получаем пользователя по userId из запроса
            User user = userRepo.findById(input.getUserId())
                    .orElseThrow(() -> new UserNotFoundException("User not found with id " + input.getUserId()));

            // Устанавливаем пользователя в сущность Story
            story.setUser(user);

            // Обновляем историю в репозитории
            return storyRepo
                    .update(story)
                    .map(storyMapper::ToStoryResponseTo)
                    .orElseThrow();
        } catch (DataIntegrityViolationException e) {
            throw new UniqueConstraintViolationException("Title is already taken");
        }
    }


    public boolean delete(long id) {
        Story story = storyRepo.findById(id).orElseThrow(() -> new StoryNotFoundException("Story with id " + id + " not found"));
        List<Label> labels = story.getLabels();

        // Удаляем связь истории с метками
        if( storyRepo.delete(id)){
            for (Label label : labels) {
                if (label.getStories().isEmpty()) {
                    labelRepo.delete(label.getId()); // Если метка больше не привязана к истории, удаляем её
                }
            }
            return true;
        } else {
            return false;
        }


    }



}
