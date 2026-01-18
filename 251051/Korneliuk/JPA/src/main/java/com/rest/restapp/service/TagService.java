package com.rest.restapp.service;

import com.rest.restapp.dto.request.TagRequestTo;
import com.rest.restapp.dto.response.TagResponseTo;
import com.rest.restapp.exception.NotFoundException;
import com.rest.restapp.mapper.TagMapper;
import com.rest.restapp.repository.TagRepository;
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

    TagRepository tagRepository;
    TagMapper mapper;

    @Transactional
    public TagResponseTo createTag(TagRequestTo requestTo) {
        validateTagRequest(requestTo);
        var tag = mapper.toEntity(requestTo);
        var savedTag = tagRepository.save(tag);
        return mapper.toResponseTo(savedTag);
    }

    public TagResponseTo getTagById(Long id) {
        var tag = tagRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Tag with id " + id + " not found"));
        return mapper.toResponseTo(tag);
    }

    public List<TagResponseTo> getAllTags() {
        return tagRepository.findAll().stream()
                .map(mapper::toResponseTo)
                .toList();
    }

    @Transactional
    public TagResponseTo updateTag(Long id, TagRequestTo requestTo) {
        validateTagRequest(requestTo);
        var existingTag = tagRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Tag with id " + id + " not found"));

        mapper.updateEntityFromDto(requestTo, existingTag);
        var updatedTag = tagRepository.save(existingTag);
        return mapper.toResponseTo(updatedTag);
    }

    @Transactional
    public void deleteTag(Long id) {
        if (!tagRepository.existsById(id)) {
            throw new NotFoundException("Tag with id " + id + " not found");
        }
        tagRepository.deleteById(id);
    }

    private void validateTagRequest(TagRequestTo requestTo) {
        if (requestTo == null) {
            throw new ValidationException("Tag request cannot be null");
        }
        if (requestTo.name() == null || requestTo.name().trim().isEmpty()) {
            throw new ValidationException("Name is required");
        }
    }
}