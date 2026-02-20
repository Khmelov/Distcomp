package com.sergey.orsik.service.impl;

import com.sergey.orsik.dto.request.LabelRequestTo;
import com.sergey.orsik.dto.response.LabelResponseTo;
import com.sergey.orsik.entity.Label;
import com.sergey.orsik.exception.EntityNotFoundException;
import com.sergey.orsik.mapper.LabelMapper;
import com.sergey.orsik.repository.CrudRepository;
import com.sergey.orsik.service.LabelService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LabelServiceImpl implements LabelService {

    private final CrudRepository<Label> repository;
    private final LabelMapper mapper;

    public LabelServiceImpl(CrudRepository<Label> repository, LabelMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public List<LabelResponseTo> findAll() {
        return repository.findAll().stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public LabelResponseTo findById(Long id) {
        Label entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Label", id));
        return mapper.toResponse(entity);
    }

    @Override
    public LabelResponseTo create(LabelRequestTo request) {
        Label entity = mapper.toEntity(request);
        entity.setId(null);
        Label saved = repository.save(entity);
        return mapper.toResponse(saved);
    }

    @Override
    public LabelResponseTo update(Long id, LabelRequestTo request) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Label", id);
        }
        Label entity = mapper.toEntity(request);
        entity.setId(id);
        Label saved = repository.save(entity);
        return mapper.toResponse(saved);
    }

    @Override
    public void deleteById(Long id) {
        if (!repository.deleteById(id)) {
            throw new EntityNotFoundException("Label", id);
        }
    }
}
