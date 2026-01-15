package com.example.task310.service;

import com.example.task310.domain.Writer;
import com.example.task310.dto.request.WriterRequestTo;
import com.example.task310.dto.response.WriterResponseTo;
import com.example.task310.error.NotFoundException;
import com.example.task310.repo.WriterRepo;
import org.springframework.stereotype.Service;

@Service
public class WriterService {

    private final WriterRepo repo;

    public WriterService(WriterRepo repo) {
        this.repo = repo;
    }

    public WriterResponseTo create(WriterRequestTo r) {
        var created = repo.create(new Writer(null, r.login(), r.password(), r.firstname(), r.lastname()));
        return toDto(created);
    }

    public java.util.List<WriterResponseTo> getAll() {
        return repo.findAll().stream().map(this::toDto).toList();
    }

    public WriterResponseTo getById(long id) {
        var w = repo.find(id).orElseThrow(() -> new NotFoundException("Writer not found: " + id));
        return toDto(w);
    }

    public WriterResponseTo update(long id, WriterRequestTo r) {
        if (!repo.exists(id)) throw new NotFoundException("Writer not found: " + id);
        var updated = repo.update(id, new Writer(id, r.login(), r.password(), r.firstname(), r.lastname()));
        return toDto(updated);
    }

    public void delete(long id) {
        if (!repo.exists(id)) throw new NotFoundException("Writer not found: " + id);
        repo.delete(id);
    }

    private WriterResponseTo toDto(Writer w) {
        return new WriterResponseTo(w.id(), w.login(), w.firstname(), w.lastname());
    }
}
