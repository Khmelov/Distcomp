package com.blog.service;

import com.blog.dto.TagRequestTo;
import com.blog.dto.TagResponseTo;
import com.blog.entity.Tag;
import com.blog.exception.EntityNotFoundException;
import com.blog.mapper.TagMapper;
import com.blog.repository.TagRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TagService {

    private final TagRepository tagRepository;
    private final TagMapper tagMapper;

    public TagService(TagRepository tagRepository, TagMapper tagMapper) {
        this.tagRepository = tagRepository;
        this.tagMapper = tagMapper;
    }

    public List<TagResponseTo> findAll() {
        return tagRepository.findAll().stream()
                .map(tagMapper::entityToResponseTo)
                .collect(Collectors.toList());
    }

    public TagResponseTo findById(Long id) {
        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tag not found with id: " + id));
        return tagMapper.entityToResponseTo(tag);
    }

    public TagResponseTo create(TagRequestTo request) {
        // Check if tag with same name already exists
        tagRepository.findByName(request.getName()).ifPresent(tag -> {
            throw new IllegalArgumentException("Tag with name '" + request.getName() + "' already exists");
        });

        Tag tag = tagMapper.requestToToEntity(request);
        tag.setCreated(java.time.LocalDateTime.now());
        tag.setModified(java.time.LocalDateTime.now());
        Tag savedTag = tagRepository.save(tag);
        return tagMapper.entityToResponseTo(savedTag);
    }

    public TagResponseTo update(Long id, TagRequestTo request) {
        Tag existingTag = tagRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tag not found with id: " + id));

        // Check if another tag with same name already exists
        tagRepository.findByName(request.getName())
                .filter(tag -> !tag.getId().equals(id))
                .ifPresent(tag -> {
                    throw new IllegalArgumentException("Tag with name '" + request.getName() + "' already exists");
                });

        tagMapper.updateEntityFromRequest(request, existingTag);
        existingTag.setModified(java.time.LocalDateTime.now());
        Tag updatedTag = tagRepository.save(existingTag);
        return tagMapper.entityToResponseTo(updatedTag);
    }

    public void deleteById(Long id) {
        if (!tagRepository.existsById(id)) {
            throw new EntityNotFoundException("Tag not found with id: " + id);
        }
        tagRepository.deleteById(id);
    }
}