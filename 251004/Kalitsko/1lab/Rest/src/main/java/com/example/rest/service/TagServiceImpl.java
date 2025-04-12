package com.example.rest.service;

import com.example.rest.dto.TagRequestTo;
import com.example.rest.dto.TagResponseTo;
import com.example.rest.dto.TagUpdate;
import com.example.rest.entity.Tag;
import com.example.rest.mapper.TagMapper;
import com.example.rest.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {

    private TagRepository tagRepository;
    private TagMapper tagMapper;

    @Autowired
    public TagServiceImpl(TagRepository tagRepository, TagMapper tagMapper) {
        this.tagRepository = tagRepository;
        this.tagMapper = tagMapper;
    }

    @Override
    public TagResponseTo create(TagRequestTo tag) {
        return tagMapper.toResponse(tagRepository.create(tagMapper.toEntity(tag)));

    }

    @Override
    public TagResponseTo update(TagUpdate updatedTag) {
        Tag tag = tagRepository.findById(updatedTag.getId())
                .orElseThrow(() -> new IllegalArgumentException("Tag not found"));

        if (updatedTag.getId() != null) {
            tag.setId(updatedTag.getId());
        }
        if (updatedTag.getName() != null) {
            tag.setName(updatedTag.getName());
        }

        return tagMapper.toResponse(tagRepository.update(tag));
    }

    @Override
    public void deleteById(Long id) {
        tagRepository.deleteById(id);
    }

    @Override
    public List<TagResponseTo> findAll() {
        return tagRepository.findAll()
                .stream()
                .map(tagMapper::toResponse)
                .toList();
    }

    @Override
    public Optional<TagResponseTo> findById(Long id) {
        return tagRepository.findById(id)
                .map(tagMapper::toResponse);
    }
}
