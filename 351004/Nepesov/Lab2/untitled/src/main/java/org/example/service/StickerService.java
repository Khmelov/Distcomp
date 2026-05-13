package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.StickerRequestTo;
import org.example.dto.StickerResponseTo;
import org.example.exception.EntityNotFoundException;
import org.example.mapper.StickerMapper;
import org.example.model.Sticker;
import org.example.repository.StickerRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class StickerService {
    private final StickerRepository repository;
    private final StickerMapper mapper;

    public StickerResponseTo create(StickerRequestTo request) {
        Sticker entity = mapper.toEntity(request);
        return mapper.toResponse(repository.save(entity));
    }

    @Transactional(readOnly = true)
    public List<StickerResponseTo> findAll(int page, int size, String sortBy) {
        return repository.findAll(PageRequest.of(page, size, Sort.by(sortBy)))
                .stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public StickerResponseTo findById(Long id) {
        return repository.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("Sticker not found"));
    }

    public StickerResponseTo update(StickerRequestTo request) {
        if (!repository.existsById(request.getId())) {
            throw new EntityNotFoundException("Sticker not found");
        }
        return mapper.toResponse(repository.save(mapper.toEntity(request)));
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Sticker not found");
        }
        repository.deleteById(id);
    }
}