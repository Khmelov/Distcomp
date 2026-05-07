package com.example.forum.service.impl;

import com.example.forum.dto.request.MarkRequestTo;
import com.example.forum.dto.response.MarkResponseTo;
import com.example.forum.entity.Mark;
import com.example.forum.exception.BadRequestException;
import com.example.forum.exception.ForbiddenException;
import com.example.forum.exception.NotFoundException;
import com.example.forum.mapper.MarkMapper;
import com.example.forum.repository.MarkRepository;
import com.example.forum.service.MarkService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional
    public MarkResponseTo create(MarkRequestTo request) {
        // Используем BadRequestException с кодом 40321 для корректного перехвата
        if (repository.existsByName(request.getName())) {
            throw new BadRequestException("Mark with this name already exists", "40321");
        }
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
    public Page<MarkResponseTo> getAll(Pageable pageable) {
        return repository.findAll(pageable)
                .map(mapper::toResponse);
    }


    @Override
    @Transactional
    public MarkResponseTo update(Long id, MarkRequestTo request) {
        validate(request);
        Mark existing = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Mark not found", "40408"));

        // Проверяем, не занято ли новое имя другой меткой
        if (!existing.getName().equals(request.getName()) && repository.existsByName(request.getName())) {
            throw new BadRequestException("Mark with this name already exists", "40321");
        }

        existing.setName(request.getName());
        Mark updated = repository.save(existing);
        return mapper.toResponse(updated);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new NotFoundException("Mark not found", "40409");
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

