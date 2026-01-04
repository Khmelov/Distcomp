package com.task.rest.service;

import com.task.rest.dto.MarkRequestTo;
import com.task.rest.dto.MarkResponseTo;
import com.task.rest.exception.ResourceNotFoundException;
import com.task.rest.exception.ValidationException;
import com.task.rest.model.Mark;
import com.task.rest.repository.MarkRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class MarkService {

    private final MarkRepository markRepository;

    @Transactional(readOnly = true)
    public MarkResponseTo getById(Long id) {
        log.info("Getting mark by id: {}", id);
        Mark mark = markRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Mark not found with id: {}", id);
                    return new ResourceNotFoundException("Mark not found with id: " + id);
                });
        return mapToResponse(mark);
    }

    @Transactional(readOnly = true)
    public Page<MarkResponseTo> getAll(Pageable pageable) {
        log.info("Getting all marks with pageable: {}", pageable);
        return markRepository.findAll(pageable)
                .map(this::mapToResponse);
    }

    public MarkResponseTo create(MarkRequestTo requestTo) {
        log.info("Creating new mark with name: {}", requestTo.getName());

        if (markRepository.findByName(requestTo.getName()).isPresent()) {
            log.error("Mark with name already exists: {}", requestTo.getName());
            throw new ValidationException("Mark with name already exists: " + requestTo.getName());
        }

        Mark mark = new Mark();
        mark.setName(requestTo.getName());

        mark = markRepository.save(mark);
        log.info("Mark created with id: {}", mark.getId());
        return mapToResponse(mark);
    }

    public MarkResponseTo update(Long id, MarkRequestTo requestTo) {
        log.info("Updating mark with id: {}", id);

        Mark mark = markRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Mark not found with id: {}", id);
                    return new ResourceNotFoundException("Mark not found with id: " + id);
                });

        if (!mark.getName().equals(requestTo.getName()) &&
                markRepository.findByName(requestTo.getName()).isPresent()) {
            log.error("Mark with name already exists: {}", requestTo.getName());
            throw new ValidationException("Mark with name already exists: " + requestTo.getName());
        }

        mark.setName(requestTo.getName());
        mark = markRepository.save(mark);
        log.info("Mark updated with id: {}", id);
        return mapToResponse(mark);
    }

    public void delete(Long id) {
        log.info("Deleting mark with id: {}", id);

        if (!markRepository.existsById(id)) {
            log.error("Mark not found with id: {}", id);
            throw new ResourceNotFoundException("Mark not found with id: " + id);
        }

        markRepository.deleteById(id);
        log.info("Mark deleted with id: {}", id);
    }

    private MarkResponseTo mapToResponse(Mark mark) {
        return new MarkResponseTo(
                mark.getId(),
                mark.getName()
        );
    }
}