package com.distcomp.publisher.sticker.service;

import com.distcomp.publisher.sticker.domain.Sticker;
import com.distcomp.publisher.sticker.dto.StickerRequest;
import com.distcomp.publisher.sticker.dto.StickerResponse;
import com.distcomp.publisher.sticker.repo.StickerRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class StickerService {

    private final StickerRepository repository;

    public StickerService(StickerRepository repository) {
        this.repository = repository;
    }

    public StickerResponse create(StickerRequest request) {
        Sticker sticker = new Sticker();
        sticker.setName(request.getName());
        return toResponse(repository.save(sticker));
    }

    public Optional<StickerResponse> get(long id) {
        return repository.findById(id).map(this::toResponse);
    }

    public List<StickerResponse> list() {
        return repository.findAll().stream().map(this::toResponse).toList();
    }

    public Optional<StickerResponse> update(long id, StickerRequest request) {
        return repository.findById(id).map(existing -> {
            existing.setName(request.getName());
            return toResponse(repository.save(existing));
        });
    }

    public boolean delete(long id) {
        if (!repository.existsById(id)) {
            return false;
        }
        repository.deleteById(id);
        return true;
    }

    private StickerResponse toResponse(Sticker s) {
        return new StickerResponse(s.getId() != null ? s.getId() : 0, s.getName());
    }
}
