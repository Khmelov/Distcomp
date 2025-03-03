package com.example.lab1.service;

import com.example.lab1.controller.exception.EntityNotFoundException;
import com.example.lab1.dto.TagRequestTo;
import com.example.lab1.dto.TagResponseTo;
import com.example.lab1.entity.Tag;
import com.example.lab1.mapper.TagMapper;
import com.example.lab1.repository.EntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TagService implements EntityService<TagRequestTo, TagResponseTo> {

    private final EntityRepository<Tag> tagRepository;
    private final TagMapper tagMapper = TagMapper.INSTANCE;

    @Override
    public TagResponseTo create(TagRequestTo entityDTO) {
        Tag tag = tagMapper.toEntity(entityDTO);
        Tag savedTag = tagRepository.create(tag);
        return tagMapper.toDTO(savedTag);
    }

    @Override
    public TagResponseTo getById(Long id) {
        return tagRepository.findById(id)
                .map(tagMapper::toDTO)
                .orElseThrow(() -> new EntityNotFoundException("Tag with ID " + id + " not found"));
    }

    @Override
    public List<TagResponseTo> getAll() {
        return tagRepository.findAll()
                .stream()
                .map(tagMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public TagResponseTo update(TagRequestTo entityDTO) {
        Tag tag = tagMapper.toEntity(entityDTO);
        Tag updatedTag = tagRepository.update(tag);
        return tagMapper.toDTO(updatedTag);
    }

    @Override
    public void deleteById(Long id) {
        if (tagRepository.findById(id).isEmpty()) {
            throw new EntityNotFoundException("Tag with ID " + id + " not found");
        }
        tagRepository.deleteById(id);
    }
}
