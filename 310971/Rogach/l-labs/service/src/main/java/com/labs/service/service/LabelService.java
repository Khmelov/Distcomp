package com.labs.service.service;

import com.labs.domain.entity.Label;
import com.labs.domain.repository.LabelRepository;
import com.labs.service.dto.LabelDto;
import com.labs.service.exception.ResourceNotFoundException;
import com.labs.service.exception.ValidationException;
import com.labs.service.mapper.LabelMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class LabelService {
    private final LabelRepository labelRepository;
    private final LabelMapper labelMapper;

    public LabelDto create(LabelDto labelDto) {
        validateLabelDto(labelDto);
        if (labelRepository.findByName(labelDto.getName()).isPresent()) {
            throw new ValidationException("Label with name '" + labelDto.getName() + "' already exists");
        }
        Label label = labelMapper.toEntity(labelDto);
        Label saved = labelRepository.save(label);
        return labelMapper.toDto(saved);
    }

    @Transactional(readOnly = true)
    public List<LabelDto> findAll() {
        return labelRepository.findAll().stream()
                .map(labelMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LabelDto findById(Long id) {
        Label label = labelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Label with id " + id + " not found"));
        return labelMapper.toDto(label);
    }

    public LabelDto update(Long id, LabelDto labelDto) {
        validateLabelDto(labelDto);
        Label label = labelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Label with id " + id + " not found"));
        
        if (!label.getName().equals(labelDto.getName())) {
            if (labelRepository.findByName(labelDto.getName()).isPresent()) {
                throw new ValidationException("Label with name '" + labelDto.getName() + "' already exists");
            }
        }
        
        label.setName(labelDto.getName());
        
        Label updated = labelRepository.save(label);
        return labelMapper.toDto(updated);
    }

    public void delete(Long id) {
        if (!labelRepository.existsById(id)) {
            throw new ResourceNotFoundException("Label with id " + id + " not found");
        }
        labelRepository.deleteById(id);
    }

    private void validateLabelDto(LabelDto labelDto) {
        if (labelDto == null) {
            throw new ValidationException("Label data cannot be null");
        }
        if (labelDto.getName() == null || labelDto.getName().trim().isEmpty()) {
            throw new ValidationException("Name cannot be null or empty");
        }
        if (labelDto.getName().length() < 2 || labelDto.getName().length() > 32) {
            throw new ValidationException("Name must be between 2 and 32 characters");
        }
    }
}

