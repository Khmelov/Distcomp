package com.example.demo.service;

import com.example.demo.dto.requests.IssueRequestTo;
import com.example.demo.dto.requests.MarkRequestTo;
import com.example.demo.dto.responses.IssueResponseTo;
import com.example.demo.dto.responses.MarkResponseTo;
import com.example.demo.model.Mark;
import com.example.demo.repository.MarkRepository;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;

@Service
public class MarkService {
    private final MarkRepository repository;

    public MarkService(MarkRepository repository) {
        this.repository = repository;
    }


    public MarkResponseTo create(MarkRequestTo dto) {
        Mark mark = new Mark();
        mark.setName(dto.getName());
        mark.setCreated(ZonedDateTime.now());
        mark.setModified(ZonedDateTime.now());

        Mark saved = repository.save(mark);

        return new MarkResponseTo(
                saved.getId(),
                saved.getName(),
                saved.getCreated(),
                saved.getModified()
        );
    }

    public List<MarkResponseTo> findAll() {
        return repository.findAll().stream()
                .map(m -> new MarkResponseTo(
                        m.getId(), m.getName(), m.getCreated(), m.getModified()
                ))
                .toList();
    }

    public MarkResponseTo findById(Long id) {
        Mark mark = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mark not found: " + id));
        return new MarkResponseTo(mark.getId(), mark.getName(), mark.getCreated(), mark.getModified());
    }

    public MarkResponseTo update(Long id, MarkRequestTo dto) {
        Mark existing = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mark not found: " + id));

        existing.setName(dto.getName());
        existing.setModified(ZonedDateTime.now());

        Mark updated = repository.save(existing);
        return new MarkResponseTo(updated.getId(), updated.getName(), updated.getCreated(), updated.getModified());
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Mark not found: " + id);
        }
        repository.deleteById(id);
    }
}
