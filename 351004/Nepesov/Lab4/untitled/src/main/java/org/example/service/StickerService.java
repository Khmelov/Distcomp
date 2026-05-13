package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.StickerRequestTo;
import org.example.dto.StickerResponseTo;
import org.example.mapper.StickerMapper;
import org.example.model.Sticker;
import org.example.repository.StickerRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StickerService {

    private final StickerRepository stickerRepository;
    private final StickerMapper stickerMapper;

    public List<StickerResponseTo> findAll() {
        return stickerRepository.findAll()
                .stream()
                .map(stickerMapper::toResponse)
                .collect(Collectors.toList());
    }

    public StickerResponseTo create(StickerRequestTo request) {
        Sticker entity = stickerMapper.toEntity(request);
        entity.setId(System.currentTimeMillis());
        return stickerMapper.toResponse(stickerRepository.save(entity));
    }

    public StickerResponseTo findById(Long id) {
        return stickerRepository.findById(id)
                .map(stickerMapper::toResponse)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sticker not found"));
    }

    public StickerResponseTo update(StickerRequestTo request) {
        if (!stickerRepository.existsById(request.getId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Sticker not found");
        }
        Sticker entity = stickerMapper.toEntity(request);
        entity.setId(request.getId());
        return stickerMapper.toResponse(stickerRepository.save(entity));
    }

    public void delete(Long id) {
        stickerRepository.deleteById(id);
    }

    public boolean existsById(Long id) {
        return stickerRepository.existsById(id);
    }
}