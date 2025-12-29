package com.rest.service;

import com.rest.dto.request.WriterRequestTo;
import com.rest.dto.response.WriterResponseTo;
import com.rest.entity.Writer;
import com.rest.mapper.WriterMapper;
import com.rest.repository.inmemory.InMemoryWriterRepository;
import com.rest.exception.NotFoundException;
import com.rest.exception.ValidationException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import java.util.List;

@Service
@Validated
public class WriterService {
    
	@Autowired
    private final InMemoryWriterRepository writerRepository;
	
	@Autowired
    private final WriterMapper writerMapper;
    
    public WriterService(InMemoryWriterRepository writerRepository, 
                        WriterMapper writerMapper) {
        this.writerRepository = writerRepository;
        this.writerMapper = writerMapper;
    }
    
    public WriterResponseTo create(@Valid WriterRequestTo request) {
        validateWriterRequest(request);
        
        if (writerRepository.existsByLogin(request.getLogin())) {
            throw new ValidationException("Login already exists: " + request.getLogin());
        }
        
        Writer writer = writerMapper.toEntity(request);
        Writer saved = writerRepository.save(writer);
        return writerMapper.toResponse(saved);
    }
    
    public WriterResponseTo findById(Long id) {
        Writer writer = writerRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Writer not found: " + id));
        return writerMapper.toResponse(writer);
    }
    
    public List<WriterResponseTo> findAll() {
        return writerRepository.findAll().stream()
            .map(writerMapper::toResponse)
            .toList();
    }
    
    public WriterResponseTo update(Long id, @Valid WriterRequestTo request) {
        validateWriterRequest(request);
        
        Writer existing = writerRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Writer not found: " + id));
        
        if (!existing.getLogin().equals(request.getLogin()) && 
            writerRepository.existsByLogin(request.getLogin())) {
            throw new ValidationException("Login already exists: " + request.getLogin());
        }
        
        writerMapper.updateEntity(request, existing);
        Writer updated = writerRepository.update(existing);
        return writerMapper.toResponse(updated);
    }
    
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