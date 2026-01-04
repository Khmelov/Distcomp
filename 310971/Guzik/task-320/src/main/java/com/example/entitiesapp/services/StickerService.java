package com.example.entitiesapp.services;

import com.example.entitiesapp.dto.request.StickerRequestTo;
import com.example.entitiesapp.dto.response.StickerResponseTo;
import com.example.entitiesapp.entities.Sticker;
import com.example.entitiesapp.exceptions.DuplicateResourceException;
import com.example.entitiesapp.exceptions.ResourceNotFoundException;
import com.example.entitiesapp.exceptions.ValidationException;
import com.example.entitiesapp.mappers.StickerMapper;
import com.example.entitiesapp.repositories.StickerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StickerService {
    private final StickerRepository stickerRepository;
    private final StickerMapper stickerMapper;

    public List<StickerResponseTo> getAll() {
        return stickerRepository.findAll().stream()
                .map(stickerMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    public StickerResponseTo getById(Long id) {
        Sticker sticker = stickerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sticker not found with id: " + id));
        return stickerMapper.toResponseDto(sticker);
    }

    @Transactional
    public StickerResponseTo create(StickerRequestTo dto) {
        validateStickerRequest(dto);

        if (stickerRepository.existsByName(dto.getName())) {
            throw new DuplicateResourceException("Sticker with name '" + dto.getName() + "' already exists");
        }

        Sticker sticker = stickerMapper.toEntity(dto);
        Sticker saved = stickerRepository.save(sticker);
        return stickerMapper.toResponseDto(saved);
    }

    @Transactional
    public StickerResponseTo update(Long id, StickerRequestTo dto) {
        validateStickerRequest(dto);

        Sticker existing = stickerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sticker not found with id: " + id));

        if (!existing.getName().equals(dto.getName()) &&
                stickerRepository.existsByName(dto.getName())) {
            throw new DuplicateResourceException("Sticker with name '" + dto.getName() + "' already exists");
        }

        existing.setName(dto.getName());
        Sticker updated = stickerRepository.save(existing);
        return stickerMapper.toResponseDto(updated);
    }

    @Transactional
    public void delete(Long id) {
        if (!stickerRepository.existsById(id)) {
            throw new ResourceNotFoundException("Sticker not found with id: " + id);
        }
        stickerRepository.deleteById(id);
    }

    public List<StickerResponseTo> findByArticleId(Long articleId) {
        return stickerRepository.findByArticleId(articleId).stream()
                .map(stickerMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    private void validateStickerRequest(StickerRequestTo dto) {
        if (dto.getName() == null || dto.getName().trim().isEmpty()) {
            throw new ValidationException("Name is required");
        }
        if (dto.getName().length() < 2 || dto.getName().length() > 32) {
            throw new ValidationException("Name must be between 2 and 32 characters");
        }
    }
}