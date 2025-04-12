package com.example.rest.service.Impl;

import com.example.rest.dto.tag.TagRequestTo;
import com.example.rest.dto.tag.TagResponseTo;
import com.example.rest.dto.tag.TagUpdate;
import com.example.rest.entity.Tag;
import com.example.rest.mapper.TagMapper;
import com.example.rest.repository.TagRepository;
import com.example.rest.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

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
        return tagMapper.toResponse(tagRepository.save(tagMapper.toEntity(tag)));

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

        return tagMapper.toResponse(tagRepository.save(tag));
    }

    @Override
    public void deleteById(Long id) {
        tagRepository.deleteById(id);
    }

    @Override
    public List<TagResponseTo> findAll() {
        return StreamSupport.stream(tagRepository.findAll().spliterator(), false)
                .map(tagMapper::toResponse)
                .toList();
    }

    @Override
    public Optional<TagResponseTo> findById(Long id) {
        return tagRepository.findById(id)
                .map(tagMapper::toResponse);
    }
}
