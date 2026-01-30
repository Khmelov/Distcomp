package com.sergey.orsik.service.impl;

import com.sergey.orsik.dto.request.CreatorRequestTo;
import com.sergey.orsik.dto.response.CreatorResponseTo;
import com.sergey.orsik.entity.Creator;
import com.sergey.orsik.exception.EntityNotFoundException;
import com.sergey.orsik.mapper.CreatorMapper;
import com.sergey.orsik.repository.CrudRepository;
import com.sergey.orsik.service.CreatorService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CreatorServiceImpl implements CreatorService {

    private final CrudRepository<Creator> repository;
    private final CreatorMapper mapper;

    public CreatorServiceImpl(CrudRepository<Creator> repository, CreatorMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public List<CreatorResponseTo> findAll() {
        return repository.findAll().stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public CreatorResponseTo findById(Long id) {
        Creator entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Creator", id));
        return mapper.toResponse(entity);
    }

    @Override
    public CreatorResponseTo create(CreatorRequestTo request) {
        Creator entity = mapper.toEntity(request);
        entity.setId(null);
        Creator saved = repository.save(entity);
        return mapper.toResponse(saved);
    }

    @Override
    public CreatorResponseTo update(Long id, CreatorRequestTo request) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Creator", id);
        }
        Creator entity = mapper.toEntity(request);
        entity.setId(id);
        Creator saved = repository.save(entity);
        return mapper.toResponse(saved);
    }

    @Override
    public void deleteById(Long id) {
        if (!repository.deleteById(id)) {
            throw new EntityNotFoundException("Creator", id);
        }
    }
}
