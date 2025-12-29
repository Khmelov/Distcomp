package com.rest.service;

import com.rest.dto.request.LabelRequestTo;
import com.rest.dto.response.LabelResponseTo;
import com.rest.entity.Label;
import com.rest.mapper.LabelMapper;
import com.rest.repository.inmemory.InMemoryLabelRepository;
import com.rest.exception.NotFoundException;
import com.rest.exception.ValidationException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import java.util.List;

@Service
@Validated
public class LabelService {
    
	@Autowired
    private final InMemoryLabelRepository LabelRepository;
	
	@Autowired
    private final LabelMapper LabelMapper;
    
    public LabelService(InMemoryLabelRepository LabelRepository,
                       LabelMapper LabelMapper) {
        this.LabelRepository = LabelRepository;
        this.LabelMapper = LabelMapper;
    }
    
    public LabelResponseTo create(@Valid LabelRequestTo request) {
        validateLabelRequest(request);
        
        if (LabelRepository.existsByName(request.getName())) {
            throw new ValidationException("Label Name already exists: " + request.getName());
        }
        
        Label Label = LabelMapper.toEntity(request);
        Label saved = LabelRepository.save(Label);
        return LabelMapper.toResponse(saved);
    }
    
    public LabelResponseTo findById(Long id) {
        Label Label = LabelRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Label not found: " + id));
        return LabelMapper.toResponse(Label);
    }
    
    public List<LabelResponseTo> findAll() {
        return LabelRepository.findAll().stream()
            .map(LabelMapper::toResponse)
            .toList();
    }
    
    public LabelResponseTo update(Long id, @Valid LabelRequestTo request) {
        validateLabelRequest(request);
        
        Label existing = LabelRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Label not found: " + id));
        
        if (!existing.getName().equals(request.getName()) && 
            LabelRepository.existsByName(request.getName())) {
            throw new ValidationException("Label Name already exists: " + request.getName());
        }
        
        LabelMapper.updateEntity(request, existing);
        Label updated = LabelRepository.update(existing);
        return LabelMapper.toResponse(updated);
    }
    
    public void delete(Long id) {
        if (!LabelRepository.existsById(id)) {
            throw new NotFoundException("Label not found: " + id);
        }
        LabelRepository.deleteById(id);
    }
    
    private void validateLabelRequest(LabelRequestTo request) {
        if (request.getName() == null || request.getName().length() < 2 || request.getName().length() > 32) {
            throw new ValidationException("Name must be 2-32 characters");
        }
    }
}