package com.rest.restapp.service;

import com.rest.restapp.dto.request.TagRequestToDto;
import com.rest.restapp.dto.response.TagResponseToDto;
import com.rest.restapp.exception.NotFoundException;
import com.rest.restapp.mapper.TagMapper;
import com.rest.restapp.repositry.InMemoryRepository;
import jakarta.validation.ValidationException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class TagService {

    InMemoryRepository repository;
    TagMapper mapper;

    @Transactional
    public TagResponseToDto createTag(TagRequestToDto requestTo) {
        validateTagRequest(requestTo);
        var tag = mapper.toEntity(requestTo);
        var savedTag = repository.saveTag(tag);
        return mapper.toResponseTo(savedTag);
    }

    public TagResponseToDto getTagById(Long id) {
        var tag = repository.findTagById(id)
                .orElseThrow(() -> new NotFoundException("Tag with id " + id + " not found"));
        return mapper.toResponseTo(tag);
    }

    public List<TagResponseToDto> getAllTags() {
        return repository.findAllTags().stream()
                .map(mapper::toResponseTo)
                .toList();
    }

    @Transactional
    public TagResponseToDto updateTag(Long id, TagRequestToDto requestTo) {
        validateTagRequest(requestTo);
        var existingTag = repository.findTagById(id)
                .orElseThrow(() -> new NotFoundException("Tag with id " + id + " not found"));

        mapper.updateEntityFromDto(requestTo, existingTag);
        var updatedTag = repository.saveTag(existingTag);
        return mapper.toResponseTo(updatedTag);
    }

    @Transactional
    public void deleteTag(Long id) {
        if (!repository.existsTagById(id)) {
            throw new NotFoundException("Tag with id " + id + " not found");
        }
        repository.deleteTagById(id);
    }

    private void validateTagRequest(TagRequestToDto requestTo) {
        if (requestTo == null) {
            throw new ValidationException("Tag request cannot be null");
        }
        if (requestTo.name() == null || requestTo.name().trim().isEmpty()) {
            throw new ValidationException("Name is required");
        }
    }
}