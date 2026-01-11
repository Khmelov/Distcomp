package by.rest.publisher.service;

import by.rest.publisher.config.RedisCacheService;
import by.rest.publisher.domain.Editor;
import by.rest.publisher.dto.EditorRequestTo;
import by.rest.publisher.dto.EditorResponseTo;
import by.rest.publisher.exception.ApiException;
import by.rest.publisher.mapper.EditorMapper;
import by.rest.publisher.repository.EditorRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@Slf4j
public class EditorService {
    
    private final EditorRepository editorRepository;
    private final EditorMapper editorMapper;
    private final RedisCacheService redisCacheService;
    
    public EditorService(EditorRepository editorRepository, 
                        EditorMapper editorMapper,
                        RedisCacheService redisCacheService) {
        this.editorRepository = editorRepository;
        this.editorMapper = editorMapper;
        this.redisCacheService = redisCacheService;
    }
    
    @Caching(evict = {
        @CacheEvict(value = "allEditors", allEntries = true)
    })
    public EditorResponseTo create(EditorRequestTo request) {
        validateEditorRequest(request);
        
        Optional<Editor> existingEditor = editorRepository.findByLogin(request.getLogin());
        if (existingEditor.isPresent()) {
            throw new ApiException(400, "40010", "Editor with login '" + request.getLogin() + "' already exists");
        }
        
        Editor editor = editorMapper.toEntity(request);
        editor = editorRepository.save(editor);
        EditorResponseTo response = editorMapper.toResponse(editor);
        
        // Кэшируем в Redis
        redisCacheService.cacheEditor(response.getId(), response);
        log.info("Editor cached in Redis: id={}", response.getId());
        
        return response;
    }
    
    @Cacheable(value = "allEditors", unless = "#result.isEmpty()")
    @Transactional(readOnly = true)
    public List<EditorResponseTo> getAll() {
        log.info("Fetching all editors from database");
        List<EditorResponseTo> editors = editorRepository.findAll().stream()
                .map(editorMapper::toResponse)
                .toList();
        
        // Кэшируем в Redis
        redisCacheService.cacheAllEditors(editors);
        log.info("All editors cached in Redis");
        
        return editors;
    }
    
    @Cacheable(value = "editors", key = "#id", unless = "#result == null")
    @Transactional(readOnly = true)
    public EditorResponseTo getById(Long id) {
        log.info("Fetching editor from database: id={}", id);
        Editor editor = editorRepository.findById(id)
                .orElseThrow(() -> new ApiException(404, "40401", "Editor not found with id: " + id));
        
        EditorResponseTo response = editorMapper.toResponse(editor);
        
        // Кэшируем в Redis
        redisCacheService.cacheEditor(id, response);
        log.info("Editor cached in Redis: id={}", id);
        
        return response;
    }
    
    @Caching(
        put = @CachePut(value = "editors", key = "#id"),
        evict = @CacheEvict(value = "allEditors", allEntries = true)
    )
    public EditorResponseTo update(Long id, EditorRequestTo request) {
        validateEditorRequest(request);
        
        Editor editor = editorRepository.findById(id)
                .orElseThrow(() -> new ApiException(404, "40401", "Editor not found with id: " + id));
        
        if (!editor.getLogin().equals(request.getLogin())) {
            Optional<Editor> existingEditor = editorRepository.findByLogin(request.getLogin());
            if (existingEditor.isPresent()) {
                throw new ApiException(400, "40010", "Editor with login '" + request.getLogin() + "' already exists");
            }
        }
        
        editor.setLogin(request.getLogin());
        editor.setPassword(request.getPassword());
        editor.setFirstname(request.getFirstname());
        editor.setLastname(request.getLastname());
        
        editor = editorRepository.save(editor);
        EditorResponseTo response = editorMapper.toResponse(editor);
        
        // Обновляем кэш в Redis
        redisCacheService.cacheEditor(id, response);
        redisCacheService.deleteEditor(id); // Удаляем старый кэш
        log.info("Editor cache updated in Redis: id={}", id);
        
        return response;
    }
    
    @Caching(evict = {
        @CacheEvict(value = "editors", key = "#id"),
        @CacheEvict(value = "allEditors", allEntries = true)
    })
    public void delete(Long id) {
        if (!editorRepository.existsById(id)) {
            throw new ApiException(404, "40401", "Editor not found with id: " + id);
        }
        
        // Удаляем из кэша Redis
        redisCacheService.deleteEditor(id);
        log.info("Editor deleted from Redis cache: id={}", id);
        
        editorRepository.deleteById(id);
    }
    
    @Cacheable(value = "editorsByLogin", key = "#login", unless = "#result == null")
    @Transactional(readOnly = true)
    public Optional<EditorResponseTo> findByLogin(String login) {
        log.info("Fetching editor by login from database: login={}", login);
        return editorRepository.findByLogin(login)
                .map(editorMapper::toResponse)
                .map(editor -> {
                    // Кэшируем в Redis
                    redisCacheService.cacheEditor(editor.getId(), editor);
                    return editor;
                });
    }
    
    // Метод для получения редактора с кэшированием из Redis
    @Transactional(readOnly = true)
    public EditorResponseTo getByIdWithCache(Long id) {
        // Сначала пробуем получить из Redis
        Object cachedEditor = redisCacheService.getEditor(id);
        if (cachedEditor != null) {
            log.info("Editor retrieved from Redis cache: id={}", id);
            return (EditorResponseTo) cachedEditor;
        }
        
        // Если нет в кэше, получаем из БД
        return getById(id);
    }
    
    private void validateEditorRequest(EditorRequestTo request) {
        if (request.getLogin() == null || request.getLogin().trim().isEmpty()) {
            throw new ApiException(400, "40011", "Login cannot be empty");
        }
        if (request.getLogin().length() < 2 || request.getLogin().length() > 64) {
            throw new ApiException(400, "40012", "Login must be between 2 and 64 characters");
        }
        
        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            throw new ApiException(400, "40013", "Password cannot be empty");
        }
        if (request.getPassword().length() < 8 || request.getPassword().length() > 128) {
            throw new ApiException(400, "40014", "Password must be between 8 and 128 characters");
        }
        
        if (request.getFirstname() == null || request.getFirstname().trim().isEmpty()) {
            throw new ApiException(400, "40015", "Firstname cannot be empty");
        }
        if (request.getFirstname().length() < 2 || request.getFirstname().length() > 64) {
            throw new ApiException(400, "40016", "Firstname must be between 2 and 64 characters");
        }
        
        if (request.getLastname() == null || request.getLastname().trim().isEmpty()) {
            throw new ApiException(400, "40017", "Lastname cannot be empty");
        }
        if (request.getLastname().length() < 2 || request.getLastname().length() > 64) {
            throw new ApiException(400, "40018", "Lastname must be between 2 and 64 characters");
        }
    }
}