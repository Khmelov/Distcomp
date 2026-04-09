package com.sergey.orsik.service.impl;

import com.sergey.orsik.dto.request.LabelRequestTo;
import com.sergey.orsik.dto.response.LabelResponseTo;
import com.sergey.orsik.entity.Label;
import com.sergey.orsik.exception.EntityNotFoundException;
import com.sergey.orsik.mapper.LabelMapper;
import com.sergey.orsik.repository.LabelRepository;
import com.sergey.orsik.service.LabelService;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@Transactional
public class LabelServiceImpl implements LabelService {

    private final LabelRepository repository;
    private final LabelMapper mapper;

    public LabelServiceImpl(LabelRepository repository, LabelMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public List<LabelResponseTo> findAll(int page, int size, String sortBy, String sortDir, String name) {
        Pageable pageable = PageRequest.of(page, size, buildSort(sortBy, sortDir));
        Specification<Label> spec = (root, query, cb) -> cb.conjunction();
        if (StringUtils.hasText(name)) {
            String pattern = "%" + name.toLowerCase() + "%";
            spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.get("name")), pattern));
        }
        return repository.findAll(spec, pageable).stream()
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
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Label", id);
        }
        repository.deleteById(id);
    }

    private Sort buildSort(String sortBy, String sortDir) {
        String targetField = StringUtils.hasText(sortBy) ? sortBy : "id";
        Sort.Direction direction = "desc".equalsIgnoreCase(sortDir) ? Sort.Direction.DESC : Sort.Direction.ASC;
        return Sort.by(direction, targetField);
    }
}
