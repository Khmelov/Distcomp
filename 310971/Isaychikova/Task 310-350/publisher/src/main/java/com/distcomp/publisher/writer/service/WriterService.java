package com.distcomp.publisher.writer.service;

import com.distcomp.publisher.exception.DuplicateResourceException;
import com.distcomp.publisher.sticker.repo.StickerRepository;
import com.distcomp.publisher.writer.domain.Writer;
import com.distcomp.publisher.writer.dto.WriterRequest;
import com.distcomp.publisher.writer.dto.WriterResponse;
import com.distcomp.publisher.writer.repo.WriterRepository;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WriterService {

    private final WriterRepository repository;
    private final StickerRepository stickerRepository;

    public WriterService(WriterRepository repository, StickerRepository stickerRepository) {
        this.repository = repository;
        this.stickerRepository = stickerRepository;
    }

    public WriterResponse create(WriterRequest request) {
        if (repository.existsByLogin(request.getLogin())) {
            throw new DuplicateResourceException("Writer with login '" + request.getLogin() + "' already exists");
        }

        Writer writer = new Writer();
        writer.setLogin(request.getLogin());
        writer.setPassword(request.getPassword());
        writer.setFirstname(request.getFirstname());
        writer.setLastname(request.getLastname());
        return toResponse(repository.save(writer));
    }

    public Optional<WriterResponse> get(long id) {
        return repository.findById(id).map(this::toResponse);
    }

    public List<WriterResponse> list() {
        return repository.findAll().stream().map(this::toResponse).toList();
    }

    public Optional<WriterResponse> update(long id, WriterRequest request) {
        return repository.findById(id).map(existing -> {
            existing.setLogin(request.getLogin());
            existing.setPassword(request.getPassword());
            existing.setFirstname(request.getFirstname());
            existing.setLastname(request.getLastname());
            return toResponse(repository.save(existing));
        });
    }

    @Transactional
    public boolean delete(long id) {
        Optional<Writer> writerOpt = repository.findWithArticlesById(id);
        if (writerOpt.isEmpty()) {
            return false;
        }

        Writer writer = writerOpt.get();
        Long writerId = writer.getId();

        HashSet<String> stickerNamesToDelete = new HashSet<>();
        if (writerId != null) {
            stickerNamesToDelete.addAll(Arrays.asList(
                    "red" + writerId,
                    "green" + writerId,
                    "blue" + writerId
            ));
        }

        if (writer.getArticles() != null) {
            writer.getArticles().forEach(a -> {
                if (a.getStickers() != null) {
                    a.getStickers().forEach(s -> {
                        if (s != null && s.getName() != null) {
                            stickerNamesToDelete.add(s.getName());
                        }
                    });
                }
            });
        }

        if (!stickerNamesToDelete.isEmpty()) {
            stickerRepository.deleteByNameIn(stickerNamesToDelete);
        }

        repository.delete(writer);
        return true;
    }

    private WriterResponse toResponse(Writer w) {
        return new WriterResponse(
                w.getId() != null ? w.getId() : 0,
                w.getLogin(),
                w.getPassword(),
                w.getFirstname(),
                w.getLastname()
        );
    }
}
