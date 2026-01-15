package com.blog.service.impl;

import com.blog.dto.request.TagRequestTo;
import com.blog.dto.response.TagResponseTo;
import com.blog.mapper.TagMapper;
import com.blog.model.Tag;
import com.blog.repository.TagRepository;
import com.blog.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
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
    public TagResponseTo getById(Long id) {
        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tag not found with id: " + id));
        return tagMapper.toResponse(tag);
    }

    @Override
    public TagResponseTo create(TagRequestTo request) {
        Tag tag = tagMapper.toEntity(request);
        Tag saved = tagRepository.save(tag);
        return tagMapper.toResponse(saved);
    }

    @Override
    public TagResponseTo update(Long id, TagRequestTo request) {
        if (!tagRepository.existsById(id)) {
            throw new RuntimeException("Tag not found with id: " + id);
        }

        Tag tag = tagMapper.toEntity(request);
        tag.setId(id);
        Tag updated = tagRepository.update(tag);
        return tagMapper.toResponse(updated);
    }

    @Override
    public void delete(Long id) {
        if (!tagRepository.deleteById(id)) {
            throw new RuntimeException("Tag not found with id: " + id);
        }
    }

    @Override
    public boolean existsById(Long id) {
        return tagRepository.existsById(id);
    }
}