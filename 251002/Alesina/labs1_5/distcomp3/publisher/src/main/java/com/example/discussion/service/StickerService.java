package com.example.discussion.service;

import com.example.discussion.dto.StickerRequestTo;
import com.example.discussion.dto.StickerResponseTo;
import com.example.discussion.model.Sticker;
import com.example.discussion.repository.StickerRepository;
import com.example.discussion.service.mapper.StickerMapper;
import jakarta.persistence.EntityNotFoundException;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class StickerService {

    private final StickerRepository stickerRepository;
    private final StickerMapper stickerMapper = Mappers.getMapper(StickerMapper.class);

    @Autowired
    public StickerService(StickerRepository stickerRepository) {
        this.stickerRepository = stickerRepository;
    }

    public List<StickerResponseTo> findAll() {
        return stickerRepository.findAll().stream()
                .map(stickerMapper::toDto)
                .collect(Collectors.toList());
    }

    public StickerResponseTo findById(Long id) {
        Optional<Sticker> sticker = stickerRepository.findById(id);
        return sticker.map(stickerMapper::toDto).orElse(null);
    }

    public StickerResponseTo save(StickerRequestTo stickerRequestTo) {
        Sticker sticker = stickerMapper.toEntity(stickerRequestTo);
        Sticker savedSticker = stickerRepository.save(sticker);
        return stickerMapper.toDto(savedSticker);
    }

    public StickerResponseTo update(StickerRequestTo stickerRequestTo) {
        Sticker existingSticker = stickerRepository.findById(stickerRequestTo.getId()).orElseThrow(() -> new RuntimeException("Sticker not found"));
        stickerMapper.updateEntityFromDto(stickerRequestTo, existingSticker);
        Sticker updatedSticker = stickerRepository.save(existingSticker);
        return stickerMapper.toDto(updatedSticker);
    }

    public void deleteById(Long id) {
        if (!stickerRepository.existsById(id)) {
            throw new EntityNotFoundException("Sticker not found with id " + id);
        }
        stickerRepository.deleteById(id);
    }
}