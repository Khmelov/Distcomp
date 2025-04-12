package com.lab1.lab1DC.service;

import com.lab1.lab1DC.dto.StickerRequestTo;
import com.lab1.lab1DC.dto.StickerResponseTo;
import com.lab1.lab1DC.entity.Sticker;
import com.lab1.lab1DC.repository.StickerRepositoryJPA;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StickerService {
    private final StickerRepositoryJPA stickerRepositoryJPA;
    public List<StickerResponseTo> getAllStickers() {
        return stickerRepositoryJPA.findAll().stream()
                .map(sticker -> new StickerResponseTo(sticker.getId(), sticker.getName()))
                .collect(Collectors.toList());
    }

    public StickerResponseTo getStickerById(Long id) {
        return stickerRepositoryJPA.findById(id)
                .map(sticker -> new StickerResponseTo(sticker.getId(), sticker.getName()))
                .orElseThrow(() -> new NoSuchElementException("Sticker not found"));
    }

    @Transactional
    public StickerResponseTo createSticker(Sticker sticker) {
        try {
            stickerRepositoryJPA.save(sticker);
        }catch (Exception e){
            System.out.println(e.toString());
        }
        return new StickerResponseTo(sticker.getId(), sticker.getName());
    }

    public StickerResponseTo updateSticker(Long id, StickerRequestTo request) {
        Sticker sticker = stickerRepositoryJPA.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Sticker not found"));
        sticker.setName(request.getName());
        stickerRepositoryJPA.save(sticker);
        return new StickerResponseTo(id, sticker.getName());
    }

    public void deleteSticker(Long id) {
        Sticker sticker = stickerRepositoryJPA.findById(id).orElseThrow(() -> new NoSuchElementException("Sticker no found"));
        stickerRepositoryJPA.deleteById(id);
    }
}
