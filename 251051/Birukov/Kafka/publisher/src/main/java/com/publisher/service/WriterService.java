package com.publisher.service;

import com.publisher.dto.request.WriterRequestTo;
import com.publisher.dto.response.WriterResponseTo;
import com.publisher.entity.Writer;
import com.publisher.mapper.WriterMapper;
import com.publisher.repository.WriterRepository;
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
public class WriterService {
	
	@Autowired
	private final WriterRepository writerRepository;
	
	@Autowired
	private final WriterMapper writerMapper;
	
	public WriterService(WriterRepository writerRepository, 
						WriterMapper writerMapper) {
		this.writerRepository = writerRepository;
		this.writerMapper = writerMapper;
	}
	
	@Transactional
	public WriterResponseTo create(@Valid WriterRequestTo request) {
		validateWriterRequest(request);
		
		if (writerRepository.existsByLogin(request.getLogin())) {
			throw new ValidationException("Login already exists: " + request.getLogin());
		}
		
		Writer writer = writerMapper.toEntity(request);
		Writer saved = writerRepository.save(writer);
		return writerMapper.toResponse(saved);
	}
	
	@Transactional(readOnly = true)
	public WriterResponseTo findById(Long id) {
		Writer writer = writerRepository.findById(id)
			.orElseThrow(() -> new NotFoundException("Writer not found: " + id));
		return writerMapper.toResponse(writer);
	}
	
	@Transactional(readOnly = true)
	public List<WriterResponseTo> findAll() {
		return writerRepository.findAll().stream()
			.map(writerMapper::toResponse)
			.toList();
	}
	
	@Transactional
	public WriterResponseTo update(Long id, @Valid WriterRequestTo request) {
		validateWriterRequest(request);
		
		Writer existing = writerRepository.findById(id)
			.orElseThrow(() -> new NotFoundException("Writer not found: " + id));
		
		if (!existing.getLogin().equals(request.getLogin()) && 
			writerRepository.existsByLogin(request.getLogin())) {
			throw new ValidationException("Login already exists: " + request.getLogin());
		}
		
		writerMapper.updateEntity(request, existing);
		Writer updated = writerRepository.save(existing);
		return writerMapper.toResponse(updated);
	}
	
	@Transactional
	public void delete(Long id) {
		if (!writerRepository.existsById(id)) {
			throw new NotFoundException("Writer not found: " + id);
		}
		writerRepository.deleteById(id);
	}
	
	private void validateWriterRequest(WriterRequestTo request) {
		if (request.getLogin() == null || request.getLogin().length() < 2 || request.getLogin().length() > 64) {
			throw new ValidationException("Login must be 2-64 characters");
		}
		if (request.getPassword() == null || request.getPassword().length() < 8 || request.getPassword().length() > 128) {
			throw new ValidationException("Password must be 8-128 characters");
		}
		if (request.getFirstname() == null || request.getFirstname().length() < 2 || request.getFirstname().length() > 64) {
			throw new ValidationException("Firstname must be 2-64 characters");
		}
		if (request.getLastname() == null || request.getLastname().length() < 2 || request.getLastname().length() > 64) {
			throw new ValidationException("Lastname must be 2-64 characters");
		}
	}
}