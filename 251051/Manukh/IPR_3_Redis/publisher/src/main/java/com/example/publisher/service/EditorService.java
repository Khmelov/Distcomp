package com.example.publisher.service;

import com.example.publisher.config.CacheConfig;
import com.example.publisher.dto.request.EditorRequestTo;
import com.example.publisher.dto.response.EditorResponseTo;
import com.example.publisher.entity.Editor;
import com.example.publisher.exception.NotFoundException;
import com.example.publisher.mapper.EditorMapper;
import com.example.publisher.repository.EditorRepository;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
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
public class EditorService {

    private static final Logger logger = LoggerFactory.getLogger(EditorService.class);

    @Autowired
    private EditorRepository editorRepository;

    @Autowired
    private EditorMapper editorMapper;

    @Cacheable(value = CacheConfig.CacheNames.EDITORS, key = "'all'")
    public List<EditorResponseTo> getAllEditors() {
        logger.info("Fetching all editors from database");
        List<Editor> editors = editorRepository.findAll();
        return editorMapper.toResponseList(editors);
    }

    @Cacheable(value = CacheConfig.CacheNames.EDITOR_BY_ID, key = "#id")
    public EditorResponseTo getEditorById(Long id) {
        logger.info("Fetching editor with id {} from database", id);
        Editor editor = editorRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Editor not found with id: " + id, 40401));
        return editorMapper.toResponse(editor);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = CacheConfig.CacheNames.EDITORS, allEntries = true),
            @CacheEvict(value = CacheConfig.CacheNames.EDITOR_BY_ID, key = "#result.id")
    })
    public EditorResponseTo createEditor(@Valid EditorRequestTo request) {
        logger.info("Creating new editor with login: {}", request.getLogin());

        Editor editor = editorMapper.toEntity(request);
        editor.setCreatedAt(LocalDateTime.now());
        editor.setModifiedAt(LocalDateTime.now());

        Editor savedEditor = editorRepository.save(editor);
        logger.info("Editor created with id: {}", savedEditor.getId());

        return editorMapper.toResponse(savedEditor);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = CacheConfig.CacheNames.EDITORS, allEntries = true),
            @CacheEvict(value = CacheConfig.CacheNames.EDITOR_BY_ID, key = "#id"),
            @CacheEvict(value = CacheConfig.CacheNames.STORIES_BY_EDITOR, key = "#id")
    })
    public EditorResponseTo updateEditor(Long id, @Valid EditorRequestTo request) {
        logger.info("Updating editor with id: {}", id);

        Editor existingEditor = editorRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Editor not found with id: " + id, 40401));

        editorMapper.updateEntity(request, existingEditor);
        existingEditor.setModifiedAt(LocalDateTime.now());

        Editor updatedEditor = editorRepository.save(existingEditor);
        logger.info("Editor with id {} updated", id);

        return editorMapper.toResponse(updatedEditor);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = CacheConfig.CacheNames.EDITORS, allEntries = true),
            @CacheEvict(value = CacheConfig.CacheNames.EDITOR_BY_ID, key = "#id"),
            @CacheEvict(value = CacheConfig.CacheNames.STORIES_BY_EDITOR, key = "#id", allEntries = true)
    })
    public void deleteEditor(Long id) {
        logger.info("Deleting editor with id: {}", id);

        if (!editorRepository.existsById(id)) {
            throw new NotFoundException("Editor not found with id: " + id, 40401);
        }
        editorRepository.deleteById(id);
        logger.info("Editor with id {} deleted", id);
    }

    @Cacheable(value = CacheConfig.CacheNames.STORIES_BY_EDITOR, key = "#editorId")
    public Page<EditorResponseTo> getStoriesByEditorId(Long editorId, Pageable pageable) {
        logger.info("Fetching stories for editor id {} from database", editorId);
        return editorRepository.findAll(pageable)
                .map(editorMapper::toResponse);
    }
}