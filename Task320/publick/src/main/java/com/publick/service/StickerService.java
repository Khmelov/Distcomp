package com.publick.service;

import com.publick.dto.StickerRequestTo;
import com.publick.dto.StickerResponseTo;
import com.publick.entity.Sticker;
import com.publick.repository.StickerRepository;
import com.publick.service.mapper.StickerMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StickerService {

    @Autowired
    private StickerRepository stickerRepository;

    @Autowired
    private StickerMapper stickerMapper;

    public StickerResponseTo create(StickerRequestTo request) {
        Sticker sticker = stickerMapper.toEntity(request);
        Sticker saved = stickerRepository.save(sticker);
        return stickerMapper.toResponse(saved);
    }

    public StickerResponseTo getById(Long id) {
        Sticker sticker = stickerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Sticker not found with id: " + id));
        return stickerMapper.toResponse(sticker);
    }

    public List<StickerResponseTo> getAll() {
        return stickerRepository.findAll().stream()
                .map(stickerMapper::toResponse)
                .collect(Collectors.toList());
    }

    public Page<StickerResponseTo> getAll(Pageable pageable) {
        return stickerRepository.findAll(pageable)
                .map(stickerMapper::toResponse);
    }

    public StickerResponseTo update(Long id, StickerRequestTo request) {
        Sticker existing = stickerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Sticker not found with id: " + id));

        stickerMapper.updateEntityFromDto(request, existing);
        Sticker saved = stickerRepository.save(existing);
        return stickerMapper.toResponse(saved);
    }

    public void delete(Long id) {
        if (!stickerRepository.existsById(id)) {
            throw new IllegalArgumentException("Sticker not found with id: " + id);
        }
        stickerRepository.deleteById(id);
    }
}