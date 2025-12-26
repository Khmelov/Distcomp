package com.restApp.service.impl;

import com.restApp.dto.MarkRequestTo;
import com.restApp.dto.MarkResponseTo;
import com.restApp.exception.BusinessException;
import com.restApp.mapper.MarkMapper;
import com.restApp.model.Mark;
import com.restApp.repository.MarkRepository;
import com.restApp.service.MarkService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class MarkServiceImpl implements MarkService {

    private final MarkRepository markRepository;
    private final MarkMapper markMapper;

    public MarkServiceImpl(MarkRepository markRepository, MarkMapper markMapper) {
        this.markRepository = markRepository;
        this.markMapper = markMapper;
    }

    @Override
    public MarkResponseTo create(MarkRequestTo request) {
        if (markRepository.findByName(request.getName()).isPresent()) {
            throw new BusinessException("Mark already exists", "40303");
        }
        Mark mark = markMapper.toEntity(request);
        return markMapper.toResponse(markRepository.save(mark));
    }

    @Override
    @CachePut(value = "marks", key = "#id")
    public MarkResponseTo update(Long id, MarkRequestTo request) {
        Mark mark = markRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Mark not found", "40402"));

        if (request.getName() != null && !request.getName().equals(mark.getName())) {
            if (markRepository.findByName(request.getName()).isPresent()) {
                throw new BusinessException("Mark already exists", "40303");
            }
        }

        markMapper.updateEntity(mark, request);
        return markMapper.toResponse(markRepository.save(mark));
    }

    @Override
    @CacheEvict(value = "marks", key = "#id")
    public void delete(Long id) {
        if (!markRepository.existsById(id)) {
            throw new BusinessException("Mark not found", "40402");
        }
        markRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "marks", key = "#id")
    public MarkResponseTo findById(Long id) {
        return markRepository.findById(id)
                .map(markMapper::toResponse)
                .orElseThrow(() -> new BusinessException("Mark not found", "40402"));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MarkResponseTo> findAll(Pageable pageable) {
        return markRepository.findAll(pageable)
                .map(markMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public java.util.List<MarkResponseTo> findAll() {
        return markRepository.findAll().stream()
                .map(markMapper::toResponse)
                .toList();
    }
}
