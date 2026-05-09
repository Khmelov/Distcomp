package com.example.publisher.service;

import com.example.publisher.dto.request.StickerRequestTo;
import com.example.publisher.dto.response.StickerResponseTo;
import com.example.publisher.exception.EntityNotFoundException;
import com.example.publisher.mapper.StickerMapper;
import com.example.publisher.model.Sticker;
import com.example.publisher.repository.StickerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
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
    @Caching(
            put = @CachePut(value = "sticker", key = "#result.id"),
            evict = @CacheEvict(value = "stickers_list", allEntries = true)
    )
    public StickerResponseTo create(StickerRequestTo request) {
        Sticker sticker = mapper.toEntity(request);
        Sticker saved = repository.saveAndFlush(sticker);
        return mapper.toResponse(saved);
    }

    @Cacheable(value = "stickers_list")
    public List<StickerResponseTo> getAll() {
        return repository.findAll().stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Cacheable(value = "sticker", key = "#id")
    public StickerResponseTo getById(Long id) {
        return repository.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("Sticker not found with id: " + id));
    }

    @Transactional
    @Caching(
            put = @CachePut(value = "sticker", key = "#id"),
            evict = @CacheEvict(value = "stickers_list", allEntries = true)
    )
    public StickerResponseTo update(Long id, StickerRequestTo request) {
        Sticker sticker = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Sticker not found with id: " + id));
        mapper.updateEntityFromDto(request, sticker);
        Sticker saved = repository.save(sticker);
        return mapper.toResponse(saved);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "sticker", key = "#id"),
            @CacheEvict(value = "stickers_list", allEntries = true)
    })
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Sticker not found with id: " + id);
        }
        repository.deleteById(id);
    }
}