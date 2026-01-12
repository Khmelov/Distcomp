package com.task.rest.service;

import com.task.rest.dto.MarkRequestTo;
import com.task.rest.dto.MarkResponseTo;
import com.task.rest.exception.ResourceNotFoundException;
import com.task.rest.exception.ValidationException;
import com.task.rest.mapper.MarkMapper;
import com.task.rest.model.Mark;
import com.task.rest.repository.MarkRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MarkService {

    private final MarkRepository markRepository;
    private final MarkMapper markMapper;

    public MarkResponseTo getById(Long id) {
        log.info("Getting mark by id: {}", id);
        Mark mark = markRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Mark not found with id: {}", id);
                    return new ResourceNotFoundException("Mark not found with id: " + id);
                });
        return markMapper.toResponseTo(mark);
    }

    public List<MarkResponseTo> getAllList() {
        log.info("Getting all marks as list");
        return markRepository.findAll().stream()
                .map(markMapper::toResponseTo)
                .collect(Collectors.toList());
    }

    public MarkResponseTo create(MarkRequestTo requestTo) {
        log.info("Creating mark with name: {}", requestTo.getName());

        if (markRepository.existsByName(requestTo.getName())) {
            log.error("Mark with name already exists: {}", requestTo.getName());
            throw new ValidationException("Mark with name '" + requestTo.getName() + "' already exists");
        }

        Mark mark = markMapper.toEntity(requestTo);
        Mark saved = markRepository.save(mark);
        log.info("Mark created successfully with id: {}", saved.getId());

        return markMapper.toResponseTo(saved);
    }

    public MarkResponseTo update(Long id, MarkRequestTo requestTo) {
        log.info("Updating mark with id: {}", id);

        Mark mark = markRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Mark not found with id: {}", id);
                    return new ResourceNotFoundException("Mark not found with id: " + id);
                });

        if (!mark.getName().equals(requestTo.getName()) &&
                markRepository.existsByName(requestTo.getName())) {
            log.error("Mark with name already exists: {}", requestTo.getName());
            throw new ValidationException("Mark with name '" + requestTo.getName() + "' already exists");
        }

        mark.setName(requestTo.getName());

        Mark updated = markRepository.save(mark);
        log.info("Mark updated successfully with id: {}", id);

        return markMapper.toResponseTo(updated);
    }

    public void delete(Long id) {
        log.info("Deleting mark with id: {}", id);

        if (!markRepository.existsById(id)) {
            log.error("Mark not found with id: {}", id);
            throw new ResourceNotFoundException("Mark not found with id: " + id);
        }

        markRepository.deleteById(id);
        log.info("Mark deleted successfully with id: {}", id);
    }
}
