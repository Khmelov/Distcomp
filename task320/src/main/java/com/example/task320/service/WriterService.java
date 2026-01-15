package com.example.task320.service;

import com.example.task320.domain.WriterEntity;
import com.example.task320.dto.request.WriterRequestTo;
import com.example.task320.dto.response.WriterResponseTo;
import com.example.task320.error.ForbiddenException;
import com.example.task320.error.NotFoundException;
import com.example.task320.repo.WriterRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WriterService {

    private final WriterRepository repo;

    public WriterService(WriterRepository repo) {
        this.repo = repo;
    }

    @Transactional
    public WriterResponseTo create(WriterRequestTo r) {
        if (repo.existsByLogin(r.login())) {
            throw new ForbiddenException("Duplicate login");
        }
        WriterEntity e = new WriterEntity();
        e.setLogin(r.login());
        e.setPassword(r.password());
        e.setFirstname(r.firstname());
        e.setLastname(r.lastname());
        e = repo.save(e);
        return toDto(e);
    }

    public List<WriterResponseTo> getAll() {
        return repo.findAll().stream().map(this::toDto).toList();
    }

    public WriterResponseTo getById(long id) {
        return toDto(repo.findById(id).orElseThrow(() -> new NotFoundException("Writer not found: " + id)));
    }

    @Transactional
    public WriterResponseTo update(long id, WriterRequestTo r) {
        WriterEntity e = repo.findById(id).orElseThrow(() -> new NotFoundException("Writer not found: " + id));

        // если меняют login — проверяем дубликат
        if (!e.getLogin().equals(r.login()) && repo.existsByLogin(r.login())) {
            throw new ForbiddenException("Duplicate login");
        }

        e.setLogin(r.login());
        e.setPassword(r.password());
        e.setFirstname(r.firstname());
        e.setLastname(r.lastname());

        e = repo.save(e);
        return toDto(e);
    }

    @Transactional
    public void delete(long id) {
        WriterEntity e = repo.findById(id).orElseThrow(() -> new NotFoundException("Writer not found: " + id));
        repo.delete(e); // важно delete(entity), чтобы каскады сработали
    }

    private WriterResponseTo toDto(WriterEntity e) {
        return new WriterResponseTo(e.getId(), e.getLogin(), e.getFirstname(), e.getLastname());
    }
}
