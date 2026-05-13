package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.NewsRequestTo;
import org.example.dto.NewsResponseTo;
import org.example.exception.EntityNotFoundException;
import org.example.mapper.NewsMapper;
import org.example.model.News;
import org.example.model.Sticker;
import org.example.repository.EditorRepository;
import org.example.repository.NewsRepository;
import org.example.repository.StickerRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class NewsService {
    private final NewsRepository repository;
    private final EditorRepository editorRepository;
    private final StickerRepository stickerRepository;
    private final NewsMapper mapper;

    public NewsResponseTo create(NewsRequestTo request) {
        if (!editorRepository.existsById(request.getEditorId())) {
            throw new EntityNotFoundException("Editor not found");
        }

        News entity = mapper.toEntity(request);

        // Обработка стикеров: конвертируем List<String> из запроса в List<Sticker> для базы
        if (request.getStickers() != null) {
            List<Sticker> managedStickers = request.getStickers().stream()
                    .map(stickerName -> stickerRepository.findByName(stickerName)
                            .orElseGet(() -> {
                                Sticker newSticker = new Sticker();
                                newSticker.setName(stickerName);
                                return stickerRepository.save(newSticker);
                            }))
                    .collect(Collectors.toList());
            entity.setStickers(managedStickers);
        }

        entity.setCreated(LocalDateTime.now());
        entity.setModified(LocalDateTime.now());
        return mapper.toResponse(repository.save(entity));
    }

    @Transactional(readOnly = true)
    public List<NewsResponseTo> findAll(int page, int size, String sortBy) {
        return repository.findAll(PageRequest.of(page, size, Sort.by(sortBy)))
                .stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    public NewsResponseTo update(NewsRequestTo request) {
        News existing = repository.findById(request.getId())
                .orElseThrow(() -> new EntityNotFoundException("News not found"));

        News entity = mapper.toEntity(request);

        if (request.getStickers() != null) {
            List<Sticker> managedStickers = request.getStickers().stream()
                    .map(stickerName -> stickerRepository.findByName(stickerName)
                            .orElseGet(() -> {
                                Sticker newSticker = new Sticker();
                                newSticker.setName(stickerName);
                                return stickerRepository.save(newSticker);
                            }))
                    .collect(Collectors.toList());
            entity.setStickers(managedStickers);
        }

        entity.setCreated(existing.getCreated());
        entity.setModified(LocalDateTime.now());
        return mapper.toResponse(repository.save(entity));
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("News not found");
        }
        repository.deleteById(id);
    }

    public NewsResponseTo findById(Long id) {
        return repository.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("News not found"));
    }
}