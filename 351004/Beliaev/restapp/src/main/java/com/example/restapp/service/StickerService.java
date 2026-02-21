package com.example.restapp.service;

import com.example.restapp.dto.request.StickerRequestTo;
import com.example.restapp.dto.response.StickerResponseTo;
import com.example.restapp.exception.EntityNotFoundException;
import com.example.restapp.mapper.StickerMapper;
import com.example.restapp.model.Sticker;
import com.example.restapp.repository.StickerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StickerService {
    private final StickerRepository repository;
    private final StickerMapper mapper;

    public StickerResponseTo create(StickerRequestTo request) {
        Sticker sticker = mapper.toEntity(request);
        Sticker saved = repository.save(sticker);
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

    public StickerResponseTo update(Long id, StickerRequestTo request) {
        Sticker sticker = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Sticker not found with id: " + id));
        mapper.updateEntityFromDto(request, sticker);
        repository.update(sticker);
        return mapper.toResponse(sticker);
    }

    public void delete(Long id) {
        if (!repository.deleteById(id)) {
            throw new EntityNotFoundException("Sticker not found with id: " + id);
        }
    }
}