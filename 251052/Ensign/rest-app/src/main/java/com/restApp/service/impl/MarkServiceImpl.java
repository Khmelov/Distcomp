package com.restApp.service.impl;

import com.restApp.dto.MarkRequestTo;
import com.restApp.dto.MarkResponseTo;
import com.restApp.exception.BusinessException;
import com.restApp.mapper.MarkMapper;
import com.restApp.model.Mark;
import com.restApp.repository.MarkRepository;
import com.restApp.service.MarkService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MarkServiceImpl implements MarkService {

    private final MarkRepository markRepository;
    private final MarkMapper markMapper;

    public MarkServiceImpl(MarkRepository markRepository, MarkMapper markMapper) {
        this.markRepository = markRepository;
        this.markMapper = markMapper;
    }

    @Override
    public MarkResponseTo create(MarkRequestTo request) {
        if (request.getName() == null || request.getName().isEmpty()) {
            throw new BusinessException("Mark name required", "40002");
        }
        Mark mark = markMapper.toEntity(request);
        return markMapper.toResponse(markRepository.save(mark));
    }

    @Override
    public MarkResponseTo update(Long id, MarkRequestTo request) {
        Mark mark = markRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Mark not found", "40402"));

        markMapper.updateEntity(mark, request);
        return markMapper.toResponse(markRepository.save(mark));
    }

    @Override
    public void delete(Long id) {
        if (!markRepository.existsById(id)) {
            throw new BusinessException("Mark not found", "40402");
        }
        markRepository.deleteById(id);
    }

    @Override
    public MarkResponseTo findById(Long id) {
        return markRepository.findById(id)
                .map(markMapper::toResponse)
                .orElseThrow(() -> new BusinessException("Mark not found", "40402"));
    }

    @Override
    public List<MarkResponseTo> findAll() {
        return markRepository.findAll().stream()
                .map(markMapper::toResponse)
                .collect(Collectors.toList());
    }
}
