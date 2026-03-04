package com.example.demo.service;

import com.example.demo.dto.requests.MarkRequestTo;
import com.example.demo.dto.responses.AuthorResponseTo;
import com.example.demo.dto.responses.MarkResponseTo;
import com.example.demo.exception.DuplicateException;
import com.example.demo.exception.NotFoundException;
import com.example.demo.model.Mark;
import com.example.demo.repository.MarkRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

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
        if (repository.existsByName(dto.getName())) {
            throw new DuplicateException("Mark with this title already exists");
        }

        Mark mark = mapper.toEntity(dto);
        Mark saved = repository.save(mark);
        return mapper.toMarkResponse(saved);
    }

    public List<MarkResponseTo> findAll() {
        return repository.findAll().stream()
                .map(mapper::toMarkResponse)
                .collect(Collectors.toList());
    }

    public MarkResponseTo findById(Long id) {
        Mark mark = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Mark not found"));
        return mapper.toMarkResponse(mark);
    }

    public MarkResponseTo update(Long id, MarkRequestTo dto) {
        if (repository.existsByName(dto.getName())) {
            throw new DuplicateException("Mark with this title already exists");
        }
        Mark existing = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Mark not found"));

        mapper.updateMark(dto, existing);
        Mark updated = repository.save(existing);
        return mapper.toMarkResponse(updated);
    }

    public void delete(Long id)
            throws ChangeSetPersister.NotFoundException{
        if (!repository.existsById(id)) {
            throw new NotFoundException("Mark not found");
        }
        repository.deleteById(id);
    }
}
