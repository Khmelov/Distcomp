package by.bsuir.distcomp.service;

import by.bsuir.distcomp.dto.request.EditorRequestTo;
import by.bsuir.distcomp.dto.response.EditorResponseTo;
import by.bsuir.distcomp.entity.Editor;
import by.bsuir.distcomp.exception.DuplicateException;
import by.bsuir.distcomp.exception.ResourceNotFoundException;
import by.bsuir.distcomp.mapper.EditorMapper;
import by.bsuir.distcomp.repository.impl.InMemoryEditorRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EditorService {

    private final InMemoryEditorRepository editorRepository;
    private final EditorMapper editorMapper;

    public EditorService(InMemoryEditorRepository editorRepository, EditorMapper editorMapper) {
        this.editorRepository = editorRepository;
        this.editorMapper = editorMapper;
    }

    public EditorResponseTo create(EditorRequestTo dto) {
        if (editorRepository.existsByLogin(dto.getLogin())) {
            throw new DuplicateException("Editor with login '" + dto.getLogin() + "' already exists", 40301);
        }
        Editor entity = editorMapper.toEntity(dto);
        Editor saved = editorRepository.save(entity);
        return editorMapper.toResponseDto(saved);
    }

    public EditorResponseTo getById(Long id) {
        Editor entity = editorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Editor with id " + id + " not found", 40401));
        return editorMapper.toResponseDto(entity);
    }

    public List<EditorResponseTo> getAll() {
        return editorRepository.findAll().stream()
                .map(editorMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    public EditorResponseTo update(EditorRequestTo dto) {
        if (!editorRepository.existsById(dto.getId())) {
            throw new ResourceNotFoundException("Editor with id " + dto.getId() + " not found", 40402);
        }
        if (editorRepository.existsByLoginAndIdNot(dto.getLogin(), dto.getId())) {
            throw new DuplicateException("Editor with login '" + dto.getLogin() + "' already exists", 40302);
        }
        Editor entity = editorMapper.toEntity(dto);
        Editor updated = editorRepository.update(entity);
        return editorMapper.toResponseDto(updated);
    }

    public void deleteById(Long id) {
        if (!editorRepository.existsById(id)) {
            throw new ResourceNotFoundException("Editor with id " + id + " not found", 40403);
        }
        editorRepository.deleteById(id);
    }
}
