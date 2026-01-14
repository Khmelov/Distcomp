package org.example;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class TagService {

    private final TagRepository tagRepository;
    private final TagMapper tagMapper;

    public TagService(TagRepository tagRepository,
                      TagMapper tagMapper) {
        this.tagRepository = tagRepository;
        this.tagMapper = tagMapper;
    }

    // ---------- CREATE ----------
    public TagResponseTo create(TagRequestTo dto) {
        if (tagRepository.existsByName(dto.getName())) {
            throw new IllegalArgumentException("Tag already exists");
        }

        Tag tag = tagMapper.toEntity(dto);
        Tag saved = tagRepository.save(tag);
        return tagMapper.toResponse(saved);
    }

    // ---------- READ ----------
    public TagResponseTo getById(Long id) {
        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tag not found"));
        return tagMapper.toResponse(tag);
    }

    public List<TagResponseTo> getAll() {
        return tagRepository.findAll(
                        Sort.by(Sort.Direction.DESC, "id")
                ).stream()
                .map(tagMapper::toResponse)
                .toList();
    }

    // ---------- UPDATE ----------
    public TagResponseTo update(Long id, TagRequestTo dto) {
        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tag not found"));

        tag.setName(dto.getName());
        return tagMapper.toResponse(tag);
    }

    // ---------- DELETE ----------
    public void delete(Long id) {
        if (!tagRepository.existsById(id)) {
            throw new EntityNotFoundException("Tag not found");
        }
        tagRepository.deleteById(id);
    }
}