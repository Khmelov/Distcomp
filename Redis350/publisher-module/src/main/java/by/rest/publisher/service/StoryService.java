package by.rest.publisher.service;

import by.rest.publisher.config.RedisCacheService;
import by.rest.publisher.domain.Editor;
import by.rest.publisher.domain.Story;
import by.rest.publisher.domain.Tag;
import by.rest.publisher.dto.StoryRequestTo;
import by.rest.publisher.dto.StoryResponseTo;
import by.rest.publisher.exception.ApiException;
import by.rest.publisher.mapper.StoryMapper;
import by.rest.publisher.repository.EditorRepository;
import by.rest.publisher.repository.StoryRepository;
import by.rest.publisher.repository.TagRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional
@Slf4j
public class StoryService {
    
    private final StoryRepository storyRepository;
    private final EditorRepository editorRepository;
    private final TagRepository tagRepository;
    private final StoryMapper storyMapper;
    private final RedisCacheService redisCacheService;
    
    public StoryService(StoryRepository storyRepository, 
                       EditorRepository editorRepository,
                       TagRepository tagRepository,
                       StoryMapper storyMapper,
                       RedisCacheService redisCacheService) {
        this.storyRepository = storyRepository;
        this.editorRepository = editorRepository;
        this.tagRepository = tagRepository;
        this.storyMapper = storyMapper;
        this.redisCacheService = redisCacheService;
    }
    
    @Caching(evict = {
        @CacheEvict(value = "allStories", allEntries = true)
    })
    public StoryResponseTo create(StoryRequestTo request) {
        validateStoryRequest(request);
        
        Editor editor = editorRepository.findById(request.getEditorId())
                .orElseThrow(() -> new ApiException(404, "40401", "Editor not found with id: " + request.getEditorId()));
        
        Story story = storyMapper.toEntity(request);
        story.setEditor(editor);
        story.setCreated(Instant.now());
        story.setModified(Instant.now());
        
        if (request.getTagIds() != null && !request.getTagIds().isEmpty()) {
            Set<Tag> tags = new HashSet<>(tagRepository.findAllById(request.getTagIds()));
            story.setTags(tags);
        }
        
        story = storyRepository.save(story);
        StoryResponseTo response = storyMapper.toResponse(story);
        
        // Кэшируем в Redis
        redisCacheService.cacheStory(response.getId(), response);
        log.info("Story cached in Redis: id={}", response.getId());
        
        return response;
    }
    
    @Cacheable(value = "allStories", key = "{#page, #size, #sort}", unless = "#result.isEmpty()")
    @Transactional(readOnly = true)
    public Page<StoryResponseTo> getAll(int page, int size, String sort) {
        log.info("Fetching stories from database: page={}, size={}, sort={}", page, size, sort);
        
        String[] sortParams = sort.split(",");
        String sortBy = sortParams[0];
        String direction = sortParams.length > 1 ? sortParams[1] : "asc";
        
        Sort sortObj = direction.equalsIgnoreCase("desc") 
                ? Sort.by(sortBy).descending() 
                : Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sortObj);
        Page<Story> storyPage = storyRepository.findAll(pageable);
        
        List<StoryResponseTo> content = storyPage.getContent().stream()
                .map(storyMapper::toResponse)
                .toList();
        
        Page<StoryResponseTo> response = new PageImpl<>(content, pageable, storyPage.getTotalElements());
        
        // Кэшируем отдельные истории
        content.forEach(story -> {
            redisCacheService.cacheStory(story.getId(), story);
        });
        
        return response;
    }

    @Cacheable(value = "storiesByTag", key = "#tagId", unless = "#result.isEmpty()")
@Transactional(readOnly = true)
public List<StoryResponseTo> getStoriesByTagId(Long tagId) {
    log.info("Fetching stories by tag from database: tagId={}", tagId);
    List<Story> stories = storyRepository.findAll().stream()
            .filter(story -> story.getTags().stream()
                    .anyMatch(tag -> tag.getId().equals(tagId)))
            .toList();
    
    List<StoryResponseTo> response = stories.stream()
            .map(storyMapper::toResponse)
            .toList();
    
    return response;
}
    
    @Cacheable(value = "stories", key = "#id", unless = "#result == null")
    @Transactional(readOnly = true)
    public StoryResponseTo getById(Long id) {
        log.info("Fetching story from database: id={}", id);
        Story story = storyRepository.findById(id)
                .orElseThrow(() -> new ApiException(404, "40403", "Story not found with id: " + id));
        
        StoryResponseTo response = storyMapper.toResponse(story);
        
        // Кэшируем в Redis
        redisCacheService.cacheStory(id, response);
        log.info("Story cached in Redis: id={}", id);
        
        return response;
    }
    
    @Caching(
        put = @CachePut(value = "stories", key = "#id"),
        evict = {
            @CacheEvict(value = "allStories", allEntries = true),
            @CacheEvict(value = "storiesByEditor", key = "#request.editorId")
        }
    )
    public StoryResponseTo update(Long id, StoryRequestTo request) {
        validateStoryRequest(request);
        
        Story story = storyRepository.findById(id)
                .orElseThrow(() -> new ApiException(404, "40403", "Story not found with id: " + id));
        
        Editor editor = editorRepository.findById(request.getEditorId())
                .orElseThrow(() -> new ApiException(404, "40401", "Editor not found with id: " + request.getEditorId()));
        
        story.setEditor(editor);
        story.setTitle(request.getTitle());
        story.setContent(request.getContent());
        story.setModified(Instant.now());
        
        if (request.getTagIds() != null) {
            Set<Tag> tags = new HashSet<>(tagRepository.findAllById(request.getTagIds()));
            story.setTags(tags);
        } else {
            story.setTags(new HashSet<>());
        }
        
        story = storyRepository.save(story);
        StoryResponseTo response = storyMapper.toResponse(story);
        
        // Обновляем кэш в Redis
        redisCacheService.cacheStory(id, response);
        log.info("Story cache updated in Redis: id={}", id);
        
        return response;
    }
    
    @Caching(evict = {
        @CacheEvict(value = "stories", key = "#id"),
        @CacheEvict(value = "allStories", allEntries = true),
        @CacheEvict(value = "storiesByEditor", allEntries = true)
    })
    public void delete(Long id) {
        if (!storyRepository.existsById(id)) {
            throw new ApiException(404, "40403", "Story not found with id: " + id);
        }
        
        // Удаляем из кэша Redis
        redisCacheService.deleteStory(id);
        log.info("Story deleted from Redis cache: id={}", id);
        
        storyRepository.deleteById(id);
    }
    
    @Cacheable(value = "storiesByEditor", key = "#editorId", unless = "#result.isEmpty()")
    @Transactional(readOnly = true)
    public List<StoryResponseTo> getStoriesByEditorId(Long editorId) {
        log.info("Fetching stories by editor from database: editorId={}", editorId);
        List<Story> stories = storyRepository.findAll().stream()
                .filter(story -> story.getEditor().getId().equals(editorId))
                .toList();
        
        List<StoryResponseTo> response = stories.stream()
                .map(storyMapper::toResponse)
                .toList();
        
        return response;
    }
    
    // Метод для получения истории с кэшированием из Redis
    @Transactional(readOnly = true)
    public StoryResponseTo getByIdWithCache(Long id) {
        // Сначала пробуем получить из Redis
        Object cachedStory = redisCacheService.getStory(id);
        if (cachedStory != null) {
            log.info("Story retrieved from Redis cache: id={}", id);
            return (StoryResponseTo) cachedStory;
        }
        
        // Если нет в кэше, получаем из БД
        return getById(id);
    }
    

    
    // Метод для поиска историй (для контроллера)
    @Transactional(readOnly = true)
    public Page<StoryResponseTo> search(String editorLogin, Long tagId, String title, 
                                       String content, int page, int size, String sort) {
        // Временная реализация - возвращаем все истории
        // В реальном приложении нужно использовать спецификации
        
        String[] sortParams = sort.split(",");
        String sortBy = sortParams[0];
        String direction = sortParams.length > 1 ? sortParams[1] : "asc";
        
        Sort sortObj = direction.equalsIgnoreCase("desc") 
                ? Sort.by(sortBy).descending() 
                : Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sortObj);
        
        // Простая фильтрация в памяти (для примера)
        List<Story> allStories = storyRepository.findAll();
        
        List<Story> filteredStories = allStories.stream()
                .filter(story -> {
                    boolean matches = true;
                    
                    if (editorLogin != null && !editorLogin.isEmpty()) {
                        matches = matches && story.getEditor().getLogin().contains(editorLogin);
                    }
                    
                    if (tagId != null) {
                        matches = matches && story.getTags().stream()
                                .anyMatch(tag -> tag.getId().equals(tagId));
                    }
                    
                    if (title != null && !title.isEmpty()) {
                        matches = matches && story.getTitle().toLowerCase()
                                .contains(title.toLowerCase());
                    }
                    
                    if (content != null && !content.isEmpty()) {
                        matches = matches && story.getContent().toLowerCase()
                                .contains(content.toLowerCase());
                    }
                    
                    return matches;
                })
                .toList();
        
        // Создаем пагинированный результат
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), filteredStories.size());
        
        if (start > filteredStories.size()) {
            return Page.empty(pageable);
        }
        
        List<StoryResponseTo> contentList = filteredStories.subList(start, end)
                .stream()
                .map(storyMapper::toResponse)
                .toList();
        
        return new PageImpl<>(contentList, pageable, filteredStories.size());
    }
    
    // Простой метод для получения всех историй без пагинации
    @Transactional(readOnly = true)
    public List<StoryResponseTo> getAllSimple() {
        return storyRepository.findAll().stream()
                .map(storyMapper::toResponse)
                .toList();
    }
    
    // Дополнительные методы для StoryController
    @Transactional(readOnly = true)
    public Page<StoryResponseTo> getAllWithPagination(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return storyRepository.findAll(pageable)
                .map(storyMapper::toResponse);
    }
    
    @Transactional(readOnly = true)
    public List<StoryResponseTo> getStoriesByEditorLogin(String editorLogin) {
        return storyRepository.findAll().stream()
                .filter(story -> story.getEditor().getLogin().equalsIgnoreCase(editorLogin))
                .map(storyMapper::toResponse)
                .toList();
    }
    
    @Transactional(readOnly = true)
    public List<StoryResponseTo> getStoriesByTagName(String tagName) {
        return storyRepository.findAll().stream()
                .filter(story -> story.getTags().stream()
                        .anyMatch(tag -> tag.getName().equalsIgnoreCase(tagName)))
                .map(storyMapper::toResponse)
                .toList();
    }
    
    private void validateStoryRequest(StoryRequestTo request) {
        if (request.getEditorId() == null) {
            throw new ApiException(400, "40031", "Editor ID is required");
        }
        
        if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
            throw new ApiException(400, "40032", "Title cannot be empty");
        }
        if (request.getTitle().length() < 2 || request.getTitle().length() > 64) {
            throw new ApiException(400, "40033", "Title must be between 2 and 64 characters");
        }
        
        if (request.getContent() == null || request.getContent().trim().isEmpty()) {
            throw new ApiException(400, "40034", "Content cannot be empty");
        }
        if (request.getContent().length() < 4 || request.getContent().length() > 2048) {
            throw new ApiException(400, "40035", "Content must be between 4 and 2048 characters");
        }
    }
}