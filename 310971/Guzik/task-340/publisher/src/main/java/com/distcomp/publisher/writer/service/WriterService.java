package com.distcomp.publisher.writer.service;

import com.distcomp.publisher.writer.domain.Writer;
import com.distcomp.publisher.writer.dto.WriterRequest;
import com.distcomp.publisher.writer.dto.WriterResponse;
import com.distcomp.publisher.writer.repo.WriterRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class WriterService {

    private final WriterRepository repository;

    public WriterService(WriterRepository repository) {
        this.repository = repository;
    }

    public WriterResponse create(WriterRequest request) {
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

    public boolean delete(long id) {
        if (!repository.existsById(id)) {
            return false;
        }
        repository.deleteById(id);
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
