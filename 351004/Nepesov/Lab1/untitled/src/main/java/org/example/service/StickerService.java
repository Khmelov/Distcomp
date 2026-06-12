package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.StickerRequestTo;
import org.example.dto.StickerResponseTo;
import org.example.exception.EntityNotFoundException;
import org.example.mapper.StickerMapper;
import org.example.model.Sticker;
import org.example.repository.StickerRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StickerService {
    private final StickerRepository repository;
    private final StickerMapper mapper;

    public StickerResponseTo create(StickerRequestTo request) {
        Sticker entity = mapper.toEntity(request);
        return mapper.toResponse(repository.save(entity));
    }

    public List<StickerResponseTo> findAll() {
        return repository.findAll().stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    public StickerResponseTo findById(Long id) {
        return repository.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("Sticker not found with id: " + id));
    }

    public StickerResponseTo update(StickerRequestTo request) {
        if (!repository.existsById(request.getId())) {
            throw new EntityNotFoundException("Cannot update: Sticker not found");
        }
        Sticker entity = mapper.toEntity(request);
        return mapper.toResponse(repository.save(entity));
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Cannot delete: Sticker not found");
        }
        repository.deleteById(id);
    }
}