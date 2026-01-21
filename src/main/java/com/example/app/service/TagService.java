package com.example.app.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;

import com.example.app.dto.TagRequestDTO;
import com.example.app.dto.TagResponseDTO;
import com.example.app.exception.AppException;
import com.example.app.model.Tag;
import com.example.app.repository.InMemoryTagRepository;

import java.util.List;

@Service
public class TagService {
    private final InMemoryTagRepository repo;

    public TagService(InMemoryTagRepository repo) {
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

    public TagResponseDTO createTag(@Valid TagRequestDTO request) {
        Tag tag = toEntity(request);
        Tag saved = repo.save(tag);
        return toResponse(saved);
    }

    public TagResponseDTO updateTag(@Valid TagRequestDTO request) {
        if (request.id() == null) {
            throw new AppException("ID required for update", 40003);
        }
        if (!repo.findById(request.id()).isPresent()) {
            throw new AppException("Tag not found for update", 40403);
        }
        Tag tag = toEntity(request);
        Tag updated = repo.save(tag);
        return toResponse(updated);
    }

    public void deleteTag(@NotNull Long id) {
        if (!repo.deleteById(id)) {
            throw new AppException("Tag not found for deletion", 40403);
        }
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