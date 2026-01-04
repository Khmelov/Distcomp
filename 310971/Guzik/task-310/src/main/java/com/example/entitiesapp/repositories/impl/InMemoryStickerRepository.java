package com.example.entitiesapp.repositories.impl;

import com.example.entitiesapp.entities.Sticker;
import com.example.entitiesapp.repositories.StickerRepository;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class InMemoryStickerRepository implements StickerRepository {
    private final Map<Long, Sticker> stickers = new ConcurrentHashMap<>();
    private final AtomicLong idCounter = new AtomicLong(1);

    @Override
    public Sticker save(Sticker sticker) {
        if (sticker.getId() == null) {
            sticker.setId(idCounter.getAndIncrement());
            sticker.setCreated(new Date().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime());
        }
        sticker.setModified(new Date().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime());
        stickers.put(sticker.getId(), sticker);
        return sticker;
    }

    @Override
    public Optional<Sticker> findById(Long id) {
        return Optional.ofNullable(stickers.get(id));
    }

    @Override
    public List<Sticker> findAll() {
        return new ArrayList<>(stickers.values());
    }

    @Override
    public Sticker update(Sticker sticker) {
        if (!stickers.containsKey(sticker.getId())) {
            throw new IllegalArgumentException("Sticker not found with id: " + sticker.getId());
        }
        sticker.setModified(new Date().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime());
        stickers.put(sticker.getId(), sticker);
        return sticker;
    }

    @Override
    public boolean deleteById(Long id) {
        return stickers.remove(id) != null;
    }

    @Override
    public boolean existsById(Long id) {
        return stickers.containsKey(id);
    }

    @Override
    public List<Sticker> findByArticleId(Long articleId) {
        // В реальной реализации нужно проверить связь через статьи
        return stickers.values().stream()
                .filter(sticker -> sticker.getArticles() != null &&
                        sticker.getArticles().stream().anyMatch(article -> article.getId().equals(articleId)))
                .collect(Collectors.toList());
    }

    @Override
    public List<Sticker> findByName(String name) {
        return stickers.values().stream()
                .filter(sticker -> sticker.getName().equals(name))
                .collect(Collectors.toList());
    }
}