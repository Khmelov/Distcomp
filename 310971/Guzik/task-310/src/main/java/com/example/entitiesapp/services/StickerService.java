package com.example.entitiesapp.services;

import com.example.entitiesapp.dto.request.StickerRequestTo;
import com.example.entitiesapp.dto.response.StickerResponseTo;
import com.example.entitiesapp.entities.Sticker;
import com.example.entitiesapp.exceptions.ResourceNotFoundException;
import com.example.entitiesapp.exceptions.ValidationException;
import com.example.entitiesapp.mappers.StickerMapper;
import com.example.entitiesapp.repositories.StickerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
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

    public StickerResponseTo create(StickerRequestTo dto) {
        validateStickerRequest(dto);

        Sticker sticker = stickerMapper.toEntity(dto);
        sticker.setCreated(LocalDateTime.now());
        sticker.setModified(LocalDateTime.now());

        Sticker saved = stickerRepository.save(sticker);
        return stickerMapper.toResponseDto(saved);
    }

    public StickerResponseTo update(Long id, StickerRequestTo dto) {
        validateStickerRequest(dto);

        Sticker existing = stickerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sticker not found with id: " + id));

        Sticker sticker = stickerMapper.toEntity(dto);
        sticker.setId(id);
        sticker.setCreated(existing.getCreated());
        sticker.setModified(LocalDateTime.now());
        sticker.setArticles(existing.getArticles());

        Sticker updated = stickerRepository.update(sticker);
        return stickerMapper.toResponseDto(updated);
    }

    public void delete(Long id) {
        if (!stickerRepository.existsById(id)) {
            throw new ResourceNotFoundException("Sticker not found with id: " + id);
        }
        stickerRepository.deleteById(id);
    }

    private void validateStickerRequest(StickerRequestTo dto) {
        if (dto.getName() == null || dto.getName().length() < 2 || dto.getName().length() > 32) {
            throw new ValidationException("Name must be between 2 and 32 characters");
        }
    }
}