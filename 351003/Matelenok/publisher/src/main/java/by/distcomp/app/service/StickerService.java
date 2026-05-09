package by.distcomp.app.service;

import by.distcomp.app.dto.StickerRequestTo;
import by.distcomp.app.dto.StickerResponseTo;
import by.distcomp.app.exception.ResourceNotFoundException;
import by.distcomp.app.mapper.StickerMapper;
import by.distcomp.app.model.Article;
import by.distcomp.app.model.Sticker;
import by.distcomp.app.repository.StickerRepository;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;
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

    public StickerResponseTo getStickerById(Long id) {
        Sticker sticker = stickerRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Sticker not found: " + id));

        return stickerMapper.toResponse(sticker);
    }


    public List<StickerResponseTo> getStickersPage(Pageable pageable) {
        return stickerRepository.findAll(pageable)
                .map(stickerMapper::toResponse)
                .getContent();

    }

}
