package com.blog.service.impl;

import com.blog.dto.request.TagRequestTo;
import com.blog.dto.response.TagResponseTo;
import com.blog.exception.ResourceNotFoundException;
import com.blog.mapper.TagMapper;
import com.blog.model.Tag;
import com.blog.repository.TagRepository;
import com.blog.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class TagServiceImpl implements TagService {

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private TagMapper tagMapper;

    @Override
    public List<TagResponseTo> getAll() {
        return tagRepository.findAll().stream()
                .map(tagMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Page<TagResponseTo> getAll(Pageable pageable) {
        return tagRepository.findAll(pageable)
                .map(tagMapper::toResponse);
    }

    @Override
    public TagResponseTo getById(Long id) {
        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tag not found with id: " + id));
        return tagMapper.toResponse(tag);
    }

    @Override
    public TagResponseTo create(TagRequestTo request) {
        // Проверка уникальности имени тега
        if (tagRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("Tag with name '" + request.getName() + "' already exists");
        }

        Tag tag = tagMapper.toEntity(request);
        Tag savedTag = tagRepository.save(tag);
        return tagMapper.toResponse(savedTag);
    }

    @Override
    public TagResponseTo update(Long id, TagRequestTo request) {
        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tag not found with id: " + id));

        // Проверка уникальности имени тега (если изменилось)
        if (!tag.getName().equals(request.getName()) &&
                tagRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("Tag with name '" + request.getName() + "' already exists");
        }

        tag.setName(request.getName());
        Tag updatedTag = tagRepository.save(tag);
        return tagMapper.toResponse(updatedTag);
    }

    @Override
    public void delete(Long id) {
        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tag not found with id: " + id));

        // Проверяем, не используется ли тег в каких-либо темах
        if (!tag.getTopics().isEmpty()) {
            throw new IllegalStateException("Cannot delete tag with id " + id + " because it is used in topics");
        }

        tagRepository.delete(tag);
    }

    @Override
    public boolean existsById(Long id) {
        return tagRepository.existsById(id);
    }

    @Override
    public TagResponseTo findByName(String name) {
        Tag tag = tagRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Tag not found with name: " + name));
        return tagMapper.toResponse(tag);
    }

    @Override
    public boolean existsByName(String name) {
        return tagRepository.existsByName(name);
    }
}

