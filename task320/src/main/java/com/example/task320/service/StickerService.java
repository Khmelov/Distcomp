package com.example.task320.service;

import com.example.task320.domain.StickerEntity;
import com.example.task320.dto.request.StickerRequestTo;
import com.example.task320.dto.response.StickerResponseTo;
import com.example.task320.error.ForbiddenException;
import com.example.task320.error.NotFoundException;
import com.example.task320.repo.StickerRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StickerService {

    private final StickerRepository repo;

    public StickerService(StickerRepository repo) {
        this.repo = repo;
    }

    @Transactional
    public StickerResponseTo create(StickerRequestTo r) {
        if (repo.existsByName(r.name())) {
            throw new ForbiddenException("Duplicate name");
        }
        StickerEntity s = new StickerEntity();
        s.setName(r.name());
        s = repo.save(s);
        return toDto(s);
    }

    public List<StickerResponseTo> getAll() {
        return repo.findAll().stream().map(this::toDto).toList();
    }

    public StickerResponseTo getById(long id) {
        return toDto(repo.findById(id).orElseThrow(() -> new NotFoundException("Sticker not found: " + id)));
    }

    @Transactional
    public StickerResponseTo update(long id, StickerRequestTo r) {
        StickerEntity s = repo.findById(id).orElseThrow(() -> new NotFoundException("Sticker not found: " + id));

        if (!s.getName().equals(r.name()) && repo.existsByName(r.name())) {
            throw new ForbiddenException("Duplicate name");
        }

        s.setName(r.name());
        s = repo.save(s);
        return toDto(s);
    }

    @Transactional
    public void delete(long id) {
        StickerEntity s = repo.findById(id).orElseThrow(() -> new NotFoundException("Sticker not found: " + id));
        repo.delete(s);
    }

    private StickerResponseTo toDto(StickerEntity s) {
        return new StickerResponseTo(s.getId(), s.getName());
    }
}
