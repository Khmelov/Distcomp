package com.example.publisher.service;

import com.example.publisher.dto.StickerRequestTo;
import com.example.publisher.dto.StickerResponseTo;
import com.example.publisher.entity.Sticker;
import com.example.publisher.repository.StickerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class StickerService {

    private final StickerRepository repo;

    @Transactional
    public StickerResponseTo create(StickerRequestTo dto) {
        Sticker sticker = new Sticker();
        sticker.setName(dto.getName());
        return toResponse(repo.save(sticker));
    }

    @Transactional(readOnly = true)
    public Page<StickerResponseTo> getAll(Pageable pageable) {
        return repo.findAll(pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public StickerResponseTo get(Long id) {
        Sticker sticker = repo.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Sticker not found"));
        return toResponse(sticker);
    }

    @Transactional
    public StickerResponseTo update(Long id, StickerRequestTo dto) {
        Sticker sticker = repo.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Sticker not found"));
        sticker.setName(dto.getName());
        return toResponse(repo.save(sticker));
    }

    @Transactional
    public void delete(Long id) {
        if (!repo.existsById(id)) {
            throw new NoSuchElementException("Sticker not found with id: " + id);
        }
        repo.deleteById(id);
    }

    private StickerResponseTo toResponse(Sticker sticker) {
        return new StickerResponseTo(sticker.getId(), sticker.getName());
    }
}