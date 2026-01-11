package by.rest.publisher.service;

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
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class StoryService {
    
    private final StoryRepository storyRepository;
    private final EditorRepository editorRepository;
    private final TagRepository tagRepository;
    private final StoryMapper storyMapper;
    
    public StoryService(StoryRepository storyRepository, 
                       EditorRepository editorRepository,
                       TagRepository tagRepository,
                       StoryMapper storyMapper) {
        this.storyRepository = storyRepository;
        this.editorRepository = editorRepository;
        this.tagRepository = tagRepository;
        this.storyMapper = storyMapper;
    }
    
    public StoryResponseTo create(StoryRequestTo request) {
        validateStoryRequest(request);
        
        // Проверяем существование редактора
        Editor editor = editorRepository.findById(request.getEditorId())
                .orElseThrow(() -> new ApiException(404, "40401", "Editor not found with id: " + request.getEditorId()));
        
        Story story = storyMapper.toEntity(request);
        story.setEditor(editor);
        story.setCreated(Instant.now());
        story.setModified(Instant.now());
        
        // Добавляем теги если они есть
        if (request.getTagIds() != null && !request.getTagIds().isEmpty()) {
            Set<Tag> tags = new HashSet<>(tagRepository.findAllById(request.getTagIds()));
            story.setTags(tags);
        }
        
        story = storyRepository.save(story);
        return storyMapper.toResponse(story);
    }
    
    @Transactional(readOnly = true)
    public Page<StoryResponseTo> getAll(int page, int size, String sort) {
        String[] sortParams = sort.split(",");
        String sortBy = sortParams[0];
        String direction = sortParams.length > 1 ? sortParams[1] : "asc";
        
        Sort sortObj = direction.equalsIgnoreCase("desc") 
                ? Sort.by(sortBy).descending() 
                : Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sortObj);
        return storyRepository.findAll(pageable)
                .map(storyMapper::toResponse);
    }
    
    @Transactional(readOnly = true)
    public StoryResponseTo getById(Long id) {
        Story story = storyRepository.findById(id)
                .orElseThrow(() -> new ApiException(404, "40403", "Story not found with id: " + id));
        return storyMapper.toResponse(story);
    }
    
    public StoryResponseTo update(Long id, StoryRequestTo request) {
        validateStoryRequest(request);
        
        Story story = storyRepository.findById(id)
                .orElseThrow(() -> new ApiException(404, "40403", "Story not found with id: " + id));
        
        // Проверяем существование редактора
        Editor editor = editorRepository.findById(request.getEditorId())
                .orElseThrow(() -> new ApiException(404, "40401", "Editor not found with id: " + request.getEditorId()));
        
        story.setEditor(editor);
        story.setTitle(request.getTitle());
        story.setContent(request.getContent());
        story.setModified(Instant.now());
        
        // Обновляем теги
        if (request.getTagIds() != null) {
            Set<Tag> tags = new HashSet<>(tagRepository.findAllById(request.getTagIds()));
            story.setTags(tags);
        } else {
            story.setTags(new HashSet<>());
        }
        
        story = storyRepository.save(story);
        return storyMapper.toResponse(story);
    }
    
    public void delete(Long id) {
        if (!storyRepository.existsById(id)) {
            throw new ApiException(404, "40403", "Story not found with id: " + id);
        }
        storyRepository.deleteById(id);
    }
    
    @Transactional(readOnly = true)
    public List<StoryResponseTo> getStoriesByEditorId(Long editorId) {
        List<Story> stories = storyRepository.findAll().stream()
                .filter(story -> story.getEditor().getId().equals(editorId))
                .toList();
        return stories.stream()
                .map(storyMapper::toResponse)
                .toList();
    }
    
    @Transactional(readOnly = true)
    public List<StoryResponseTo> getStoriesByTagId(Long tagId) {
        List<Story> stories = storyRepository.findAll().stream()
                .filter(story -> story.getTags().stream()
                        .anyMatch(tag -> tag.getId().equals(tagId)))
                .toList();
        return stories.stream()
                .map(storyMapper::toResponse)
                .toList();
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