package com.publisher.service;

import com.publisher.dto.request.LabelRequestTo;
import com.publisher.dto.response.LabelResponseTo;
import com.publisher.entity.Label;
import com.publisher.mapper.LabelMapper;
import com.publisher.repository.LabelRepository;
import com.publisher.exception.NotFoundException;
import com.publisher.exception.ValidationException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
@Validated
public class LabelService {
	
	@Autowired
	private final LabelRepository labelRepository;
	
	@Autowired
	private final LabelMapper labelMapper;
	
	public LabelService(LabelRepository labelRepository,
					   LabelMapper labelMapper) {
		this.labelRepository = labelRepository;
		this.labelMapper = labelMapper;
	}
	
	@Transactional
	public LabelResponseTo create(@Valid LabelRequestTo request) {
		validateLabelRequest(request);
		
		if (labelRepository.existsByName(request.getName())) {
			throw new ValidationException("Label Name already exists: " + request.getName());
		}
		
		Label label = labelMapper.toEntity(request);
		Label saved = labelRepository.save(label);
		return labelMapper.toResponse(saved);
	}
	
	@Transactional
	public LabelResponseTo findById(Long id) {
		Label label = labelRepository.findById(id)
			.orElseThrow(() -> new NotFoundException("Label not found: " + id));
		return labelMapper.toResponse(label);
	}
	
	@Transactional
	public List<LabelResponseTo> findAll() {
		return labelRepository.findAll().stream()
			.map(labelMapper::toResponse)
			.toList();
	}
	
	@Transactional
	public LabelResponseTo update(Long id, @Valid LabelRequestTo request) {
		validateLabelRequest(request);
		
		Label existing = labelRepository.findById(id)
			.orElseThrow(() -> new NotFoundException("Label not found: " + id));
		
		if (!existing.getName().equals(request.getName()) && 
			labelRepository.existsByName(request.getName())) {
			throw new ValidationException("Label Name already exists: " + request.getName());
		}
		
		labelMapper.updateEntity(request, existing);
		Label updated = labelRepository.save(existing);
		return labelMapper.toResponse(updated);
	}
	
	@Transactional
	public void delete(Long id) {
		if (!labelRepository.existsById(id)) {
			throw new NotFoundException("Label not found: " + id);
		}
		labelRepository.deleteById(id);
	}
	
	@Transactional
	private void validateLabelRequest(LabelRequestTo request) {
		if (request.getName() == null || request.getName().length() < 2 || request.getName().length() > 32) {
			throw new ValidationException("Name must be 2-32 characters");
		}
	}
}