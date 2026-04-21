package by.bsuir.distcomp.service;

import by.bsuir.distcomp.dto.request.EditorRequestTo;
import by.bsuir.distcomp.dto.response.EditorResponseTo;
import by.bsuir.distcomp.entity.Editor;
import by.bsuir.distcomp.exception.DuplicateException;
import by.bsuir.distcomp.exception.ResourceNotFoundException;
import by.bsuir.distcomp.mapper.EditorMapper;
import by.bsuir.distcomp.model.EditorRole;
import by.bsuir.distcomp.repository.EditorRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class EditorService {

    private final EditorRepository editorRepository;
    private final EditorMapper editorMapper;
    private final PasswordEncoder passwordEncoder;

    public EditorService(EditorRepository editorRepository, EditorMapper editorMapper, PasswordEncoder passwordEncoder) {
        this.editorRepository = editorRepository;
        this.editorMapper = editorMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Caching(put = @CachePut(value = "editors", key = "#result.id"),
            evict = @CacheEvict(value = "editors", key = "'all'"))
    public EditorResponseTo create(EditorRequestTo dto) {
        if (editorRepository.existsByLogin(dto.getLogin())) {
            throw new DuplicateException("Editor with login '" + dto.getLogin() + "' already exists", 40301);
        }
        Editor entity = editorMapper.toEntity(dto);
        entity.setRole(EditorRole.CUSTOMER);
        entity.setPassword(encodePassword(dto.getPassword()));
        Editor saved = editorRepository.save(entity);
        return editorMapper.toResponseDto(saved);
    }

    @Cacheable(value = "editors", key = "#id")
    @Transactional(readOnly = true)
    public EditorResponseTo getById(Long id) {
        Editor entity = editorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Editor with id " + id + " not found", 40401));
        return editorMapper.toResponseDto(entity);
    }

    @Cacheable(value = "editors", key = "'all'")
    @Transactional(readOnly = true)
    public List<EditorResponseTo> getAll() {
        return editorRepository.findAll().stream()
                .map(editorMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Caching(put = @CachePut(value = "editors", key = "#result.id"),
            evict = @CacheEvict(value = "editors", key = "'all'"))
    public EditorResponseTo update(EditorRequestTo dto) {
        Editor existing = editorRepository.findById(dto.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Editor with id " + dto.getId() + " not found", 40402));
        if (editorRepository.existsByLoginAndIdNot(dto.getLogin(), dto.getId())) {
            throw new DuplicateException("Editor with login '" + dto.getLogin() + "' already exists", 40302);
        }
        editorMapper.updateEntityFromDto(dto, existing);
        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            existing.setPassword(encodePassword(dto.getPassword()));
        }
        Editor updated = editorRepository.save(existing);
        return editorMapper.toResponseDto(updated);
    }

    @Caching(evict = {
            @CacheEvict(value = "editors", key = "#id"),
            @CacheEvict(value = "editors", key = "'all'")
    })
    public void deleteById(Long id) {
        if (!editorRepository.existsById(id)) {
            throw new ResourceNotFoundException("Editor with id " + id + " not found", 40403);
        }
        editorRepository.deleteById(id);
    }

    private String encodePassword(String raw) {
        if (raw == null) {
            return null;
        }
        if (raw.startsWith("$2a$") || raw.startsWith("$2b$") || raw.startsWith("$2y$")) {
            return raw;
        }
        return passwordEncoder.encode(raw);
    }
}
