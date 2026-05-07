package com.example.discussion.service;

import com.example.discussion.dto.request.StickerRequestTo;
import com.example.discussion.dto.response.StickerResponseTo;
import com.example.discussion.exception.EntityNotFoundException;
import com.example.discussion.mapper.StickerMapper;
import com.example.discussion.model.Sticker;
import com.example.discussion.repository.StickerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StickerService {
    private final StickerRepository repository;
    private final StickerMapper mapper;

    @Transactional
    public StickerResponseTo create(StickerRequestTo request) {
        Sticker sticker = mapper.toEntity(request);
        Sticker saved = repository.saveAndFlush(sticker);
        return mapper.toResponse(saved);
    }

    public List<StickerResponseTo> getAll() {
        return repository.findAll().stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    public StickerResponseTo getById(Long id) {
        return repository.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("Sticker not found with id: " + id));
    }

    @Transactional
    public StickerResponseTo update(Long id, StickerRequestTo request) {
        Sticker sticker = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Sticker not found with id: " + id));
        mapper.updateEntityFromDto(request, sticker);
        Sticker saved = repository.save(sticker);
        return mapper.toResponse(saved);
    }

    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Sticker not found with id: " + id);
        }
        repository.deleteById(id);
    }
}