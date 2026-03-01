package com.example.demo.service;

import com.example.demo.dto.requests.MarkRequestTo;
import com.example.demo.dto.responses.MarkResponseTo;
import com.example.demo.model.Mark;
import com.example.demo.repository.MarkRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;

@Service
@Transactional
public class MarkService {
    private final MarkRepository repository;
    private final EntityMapper mapper;

    public MarkService(MarkRepository repository, EntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }


    public MarkResponseTo create(MarkRequestTo dto) {
        Mark mark = mapper.toEntity(dto);
        Mark saved = repository.save(mark);
        return mapper.toMarkResponse(saved);
    }

    public Page<MarkResponseTo> findAll(Pageable pageable) {
        return repository.findAll(pageable)
                .map(mapper::toMarkResponse);
    }

    public MarkResponseTo findById(Long id) {
        Mark mark = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Mark not found: " + id));
        return mapper.toMarkResponse(mark);
    }

    public MarkResponseTo update(Long id, MarkRequestTo dto) {
        Mark existing = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Mark not found: " + id));

        mapper.updateMark(dto, existing);
        Mark updated = repository.save(existing);
        return mapper.toMarkResponse(updated);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new NotFoundException("Mark not found: " + id);
        }
        repository.deleteById(id);
    }
}
