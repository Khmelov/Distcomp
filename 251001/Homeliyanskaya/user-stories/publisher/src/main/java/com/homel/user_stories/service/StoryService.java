package com.homel.user_stories.service;

import com.homel.user_stories.dto.StoryRequestTo;
import com.homel.user_stories.dto.StoryResponseTo;
import com.homel.user_stories.exception.EntityNotFoundException;
import com.homel.user_stories.mapper.StoryMapper;
import com.homel.user_stories.model.Story;
import com.homel.user_stories.model.User;
import com.homel.user_stories.repository.StoryRepository;
import com.homel.user_stories.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StoryService {

    private final StoryRepository storyRepository;
    private final UserRepository userRepository;
    private final RedisCacheService cacheService;

    private static final String STORY_CACHE_PREFIX = "story::";

    @Autowired
    public StoryService(StoryRepository storyRepository, UserRepository userRepository, RedisCacheService cacheService) {
        this.storyRepository = storyRepository;
        this.userRepository = userRepository;
        this.cacheService = cacheService;
    }

    public StoryResponseTo createStory(StoryRequestTo storyRequest) {
        Story story = StoryMapper.INSTANCE.toEntity(storyRequest);

        User user = userRepository.findById(storyRequest.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        story.setUser(user);

        Story savedStory = storyRepository.save(story);

        cacheService.put(STORY_CACHE_PREFIX + savedStory.getId(), StoryMapper.INSTANCE.toResponse(savedStory));

        return StoryMapper.INSTANCE.toResponse(savedStory);
    }

    public StoryResponseTo getStory(Long id) {
        String cacheKey = STORY_CACHE_PREFIX + id;
        StoryResponseTo cachedStory = cacheService.get(cacheKey, StoryResponseTo.class);

        if (cachedStory != null) {
            return cachedStory;
        }

        Story story = storyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Story not found"));

        StoryResponseTo storyResponse = StoryMapper.INSTANCE.toResponse(story);

        cacheService.put(cacheKey, storyResponse);

        return storyResponse;
    }

    public List<StoryResponseTo> getAllStories() {
        List<Story> stories = storyRepository.findAll();
        List<StoryResponseTo> storiesResponse = stories.stream()
                .map(StoryMapper.INSTANCE::toResponse)
                .toList();

        return storiesResponse;
    }

    public void deleteStory(Long id) {
        // Удаляем историю из базы
        storyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Story with id " + id + " not found"));

        storyRepository.deleteById(id);

        // Удаляем из кеша
        cacheService.evict(STORY_CACHE_PREFIX + id);
    }

    public StoryResponseTo updateStory(StoryRequestTo storyRequest) {
        Story existingStory = storyRepository.findById(storyRequest.getId())
                .orElseThrow(() -> new EntityNotFoundException("Story not found"));

        User user = userRepository.findById(storyRequest.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        existingStory.setUser(user);
        existingStory.setTitle(storyRequest.getTitle());
        existingStory.setContent(storyRequest.getContent());

        Story updatedStory = storyRepository.save(existingStory);

        // Обновляем кеш
        cacheService.put(STORY_CACHE_PREFIX + updatedStory.getId(), StoryMapper.INSTANCE.toResponse(updatedStory));

        return StoryMapper.INSTANCE.toResponse(updatedStory);
    }
}
