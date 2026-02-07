package by.distcomp.task1.service;

import by.distcomp.task1.dto.StickerRequestTo;
import by.distcomp.task1.dto.StickerResponseTo;
import by.distcomp.task1.exception.ResourceNotFoundException;
import by.distcomp.task1.mapper.StickerMapper;
import by.distcomp.task1.model.Article;
import by.distcomp.task1.model.Sticker;
import by.distcomp.task1.repository.StickerRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class StickerService {
    private final StickerRepository stickerRepository;
    private final StickerMapper stickerMapper;

    public StickerService(StickerRepository stickerRepository, StickerMapper stickerMapper) {
        this.stickerRepository = stickerRepository;
        this.stickerMapper = stickerMapper;
    }
    public StickerResponseTo createSticker(StickerRequestTo dto) {
        Sticker sticker = stickerMapper.toEntity(dto);
        Sticker saved = stickerRepository.save(sticker);
        return stickerMapper.toResponse(saved);
    }
    public StickerResponseTo updateSticker(Long id, StickerRequestTo dto) {
        Sticker sticker = stickerRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Sticker not found"));

        if (dto.name() != null) {
            sticker.setName(dto.name());
        }

        Sticker saved = stickerRepository.save(sticker);
        return stickerMapper.toResponse(saved);
    }
    public void deleteSticker(Long id) {
        Sticker sticker = stickerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sticker", "id", id));

        for (Article article : sticker.getArticles()) {
            article.getStickers().remove(sticker);
        }

        stickerRepository.deleteById(id);
    }
    public List<StickerResponseTo> getAllStickers() {
        return stickerRepository.findAll()
                .stream()
                .map(stickerMapper::toResponse)
                .collect(Collectors.toList());
    }
    public StickerResponseTo getStickerById(Long id) {
        Sticker sticker = stickerRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Sticker not found: " + id));

        return stickerMapper.toResponse(sticker);
    }
}
