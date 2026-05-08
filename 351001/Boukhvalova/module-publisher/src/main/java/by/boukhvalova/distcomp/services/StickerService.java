package by.boukhvalova.distcomp.services;

import by.boukhvalova.distcomp.dto.StickerMapper;
import by.boukhvalova.distcomp.dto.StickerRequestTo;
import by.boukhvalova.distcomp.dto.StickerResponseTo;
import by.boukhvalova.distcomp.dto.*;
import by.boukhvalova.distcomp.repositories.StickerRepository;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class StickerService {
    public final StickerRepository repImpl;
    public final StickerMapper mapper;


    public List<StickerResponseTo> getAll() {
        return repImpl.getAll().map(mapper::out).toList();
    }

    @Cacheable(value = "stickers", key = "#id")
    public StickerResponseTo getById(Long id) {
        return repImpl.get(id).map(mapper::out).orElseThrow();
    }

    @CachePut(value = "stickers", key = "#req.id")
    public StickerResponseTo create(StickerRequestTo req) {
        return repImpl.create(mapper.in(req)).map(mapper::out).orElseThrow();
    }

    @CachePut(value = "stickers", key = "#req.id")
    public StickerResponseTo update(StickerRequestTo req) {
        return repImpl.update(mapper.in(req)).map(mapper::out).orElseThrow();
    }

    @CacheEvict(value = "stickers", key = "#id")
    public void delete(Long id) {
        repImpl.delete(id);
    }
}
