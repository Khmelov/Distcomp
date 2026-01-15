package com.example.task310.service;

import com.example.task310.domain.Sticker;
import com.example.task310.dto.request.StickerRequestTo;
import com.example.task310.dto.response.StickerResponseTo;
import com.example.task310.error.NotFoundException;
import com.example.task310.repo.StickerRepo;
import org.springframework.stereotype.Service;

@Service
public class StickerService {

    private final StickerRepo repo;

    public StickerService(StickerRepo repo) {
        this.repo = repo;
    }

    public StickerResponseTo create(StickerRequestTo r) {
        var created = repo.create(new Sticker(null, r.name()));
        return toDto(created);
    }

    public java.util.List<StickerResponseTo> getAll() {
        return repo.findAll().stream().map(this::toDto).toList();
    }

    public StickerResponseTo getById(long id) {
        var s = repo.find(id).orElseThrow(() -> new NotFoundException("Sticker not found: " + id));
        return toDto(s);
    }

    public StickerResponseTo update(long id, StickerRequestTo r) {
        if (!repo.exists(id)) throw new NotFoundException("Sticker not found: " + id);
        var updated = repo.update(id, new Sticker(id, r.name()));
        return toDto(updated);
    }

    public void delete(long id) {
        if (!repo.exists(id)) throw new NotFoundException("Sticker not found: " + id);
        repo.delete(id);
    }

    private StickerResponseTo toDto(Sticker s) {
        return new StickerResponseTo(s.id(), s.name());
    }
}
