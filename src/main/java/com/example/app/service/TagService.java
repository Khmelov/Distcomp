package com.example.app.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.app.dto.TagRequestDTO;
import com.example.app.dto.TagResponseDTO;
import com.example.app.exception.AppException;
import com.example.app.model.Tag;
import com.example.app.repository.TagRepository;

import java.util.List;

@Service
public class TagService {
    private final TagRepository repo;

    public TagService(TagRepository repo) {
        this.repo = repo;
    }

    public List<TagResponseDTO> getAllTags() {
        return repo.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public TagResponseDTO getTagById(@NotNull Long id) {
        return repo.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new AppException("Tag not found", 40403));
    }

    @Transactional
    public TagResponseDTO createTag(@Valid TagRequestDTO request) {
        if (repo.existsByName(request.name())) {
            throw new AppException("Tag name already exists", 40903);
        }
        Tag tag = toEntity(request);
        Tag saved = repo.save(tag);
        return toResponse(saved);
    }

    @Transactional
    public TagResponseDTO updateTag(@Valid TagRequestDTO request) {
        if (request.id() == null) {
            throw new AppException("ID required for update", 40003);
        }
        
        Tag existingTag = repo.findById(request.id())
                .orElseThrow(() -> new AppException("Tag not found for update", 40403));
        
        // Проверяем, не занято ли новое имя другим тегом
        if (!existingTag.getName().equals(request.name()) && 
            repo.existsByName(request.name())) {
            throw new AppException("Tag name already taken", 40903);
        }
        
        existingTag.setName(request.name());
        Tag updated = repo.save(existingTag);
        return toResponse(updated);
    }

    @Transactional
    public void deleteTag(@NotNull Long id) {
        if (!repo.existsById(id)) {
            throw new AppException("Tag not found for deletion", 40403);
        }
        repo.deleteById(id);
    }

    private Tag toEntity(TagRequestDTO dto) {
        Tag tag = new Tag();
        tag.setId(dto.id());
        tag.setName(dto.name());
        return tag;
    }

    private TagResponseDTO toResponse(Tag tag) {
        return new TagResponseDTO(tag.getId(), tag.getName());
    }
}