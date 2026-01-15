package com.example.task320jpa.service;

import com.example.task320jpa.dto.request.MarkRequestTo;
import com.example.task320jpa.dto.response.MarkResponseTo;
import com.example.task320jpa.entity.Mark;
import com.example.task320jpa.exception.ResourceNotFoundException;
import com.example.task320jpa.mapper.MarkMapper;
import com.example.task320jpa.repository.MarkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class MarkService {
    private final MarkRepository markRepository;
    private final MarkMapper markMapper;
    
    public MarkResponseTo create(MarkRequestTo requestTo) {
        Mark mark = markMapper.toEntity(requestTo);
        Mark savedMark = markRepository.save(mark);
        return markMapper.toResponseTo(savedMark);
    }
    
    @Transactional(readOnly = true)
    public MarkResponseTo getById(Long id) {
        Mark mark = markRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mark", id));
        return markMapper.toResponseTo(mark);
    }
    
    @Transactional(readOnly = true)
    public Page<MarkResponseTo> getAll(Pageable pageable) {
        return markRepository.findAll(pageable).map(markMapper::toResponseTo);
    }
    
    public MarkResponseTo update(Long id, MarkRequestTo requestTo) {
        Mark existingMark = markRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mark", id));
        existingMark.setName(requestTo.getName());
        Mark updatedMark = markRepository.save(existingMark);
        return markMapper.toResponseTo(updatedMark);
    }
    
    public void delete(Long id) {
        if (!markRepository.existsById(id)) {
            throw new ResourceNotFoundException("Mark", id);
        }
        markRepository.deleteById(id);
    }
}
