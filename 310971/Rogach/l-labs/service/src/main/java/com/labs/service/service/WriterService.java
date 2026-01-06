package com.labs.service.service;

import com.labs.domain.entity.Writer;
import com.labs.domain.repository.WriterRepository;
import com.labs.service.dto.WriterDto;
import com.labs.service.exception.ResourceNotFoundException;
import com.labs.service.exception.ValidationException;
import com.labs.service.mapper.WriterMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class WriterService {
    private final WriterRepository writerRepository;
    private final WriterMapper writerMapper;

    public WriterDto create(WriterDto writerDto) {
        validateWriterDto(writerDto);
        if (writerRepository.findByLogin(writerDto.getLogin()).isPresent()) {
            throw new ValidationException("Writer with login '" + writerDto.getLogin() + "' already exists");
        }
        Writer writer = writerMapper.toEntity(writerDto);
        Writer saved = writerRepository.save(writer);
        return writerMapper.toDto(saved);
    }

    @Transactional(readOnly = true)
    public List<WriterDto> findAll() {
        return writerRepository.findAll().stream()
                .map(writerMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public WriterDto findById(Long id) {
        Writer writer = writerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Writer with id " + id + " not found"));
        return writerMapper.toDto(writer);
    }

    public WriterDto update(Long id, WriterDto writerDto) {
        validateWriterDto(writerDto);
        Writer writer = writerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Writer with id " + id + " not found"));
        
        if (!writer.getLogin().equals(writerDto.getLogin())) {
            if (writerRepository.findByLogin(writerDto.getLogin()).isPresent()) {
                throw new ValidationException("Writer with login '" + writerDto.getLogin() + "' already exists");
            }
        }
        
        writer.setLogin(writerDto.getLogin());
        writer.setPassword(writerDto.getPassword());
        writer.setFirstname(writerDto.getFirstname());
        writer.setLastname(writerDto.getLastname());
        
        Writer updated = writerRepository.save(writer);
        return writerMapper.toDto(updated);
    }

    public void delete(Long id) {
        if (!writerRepository.existsById(id)) {
            throw new ResourceNotFoundException("Writer with id " + id + " not found");
        }
        writerRepository.deleteById(id);
    }

    private void validateWriterDto(WriterDto writerDto) {
        if (writerDto == null) {
            throw new ValidationException("Writer data cannot be null");
        }
        if (writerDto.getLogin() == null || writerDto.getLogin().trim().isEmpty()) {
            throw new ValidationException("Login cannot be null or empty");
        }
        if (writerDto.getLogin().length() < 2 || writerDto.getLogin().length() > 64) {
            throw new ValidationException("Login must be between 2 and 64 characters");
        }
        if (writerDto.getPassword() == null || writerDto.getPassword().trim().isEmpty()) {
            throw new ValidationException("Password cannot be null or empty");
        }
        if (writerDto.getPassword().length() < 8 || writerDto.getPassword().length() > 128) {
            throw new ValidationException("Password must be between 8 and 128 characters");
        }
        if (writerDto.getFirstname() == null || writerDto.getFirstname().trim().isEmpty()) {
            throw new ValidationException("Firstname cannot be null or empty");
        }
        if (writerDto.getFirstname().length() < 2 || writerDto.getFirstname().length() > 64) {
            throw new ValidationException("Firstname must be between 2 and 64 characters");
        }
        if (writerDto.getLastname() == null || writerDto.getLastname().trim().isEmpty()) {
            throw new ValidationException("Lastname cannot be null or empty");
        }
        if (writerDto.getLastname().length() < 2 || writerDto.getLastname().length() > 64) {
            throw new ValidationException("Lastname must be between 2 and 64 characters");
        }
    }
}

