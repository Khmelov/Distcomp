package com.example.forum.service.impl;

import com.example.forum.dto.request.MarkRequestTo;
import com.example.forum.dto.response.MarkResponseTo;
import com.example.forum.entity.Mark;
import com.example.forum.exception.BadRequestException;
import com.example.forum.exception.NotFoundException;
import com.example.forum.mapper.MarkMapper;
import com.example.forum.repository.MarkRepository;
import com.example.forum.service.MarkService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class MarkServiceImpl implements MarkService {

    private final MarkRepository repository;
    private final MarkMapper mapper;

    public MarkServiceImpl(MarkRepository repository, MarkMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public MarkResponseTo create(MarkRequestTo request) {
        validate(request);
        Mark mark = mapper.toEntity(request);
        Mark saved = repository.save(mark);
        return mapper.toResponse(saved);
    }

    @Override
    public MarkResponseTo getById(Long id) {
        Mark mark = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Mark not found", "40421"));
        return mapper.toResponse(mark);
    }

    @Override
    public List<MarkResponseTo> getAll() {
        return repository.findAll().stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public MarkResponseTo update(Long id, MarkRequestTo request) {
        validate(request);

        Mark existing = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Mark not found", "40422"));

        existing.setName(request.getName());

        Mark updated = repository.update(existing);
        return mapper.toResponse(updated);
    }

    @Override
    public void delete(Long id) {
        if (repository.findById(id).isEmpty()) {
            throw new NotFoundException("Mark not found", "40433");
        }
        repository.deleteById(id);
    }


    private void validate(MarkRequestTo request) {
        if (!StringUtils.hasText(request.getName()) ||
                request.getName().length() < 2 ||
                request.getName().length() > 32) {
            throw new BadRequestException("Invalid mark name", "40021");
        }
    }
}
