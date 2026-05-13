package com.bsuir.distcomp.service;

import com.bsuir.distcomp.dto.WriterRequestTo;
import com.bsuir.distcomp.dto.WriterResponseTo;
import com.bsuir.distcomp.entity.Writer;
import com.bsuir.distcomp.exception.EntityAlreadyExistsException;
import com.bsuir.distcomp.exception.EntityNotFoundException;
import com.bsuir.distcomp.mapper.WriterMapper;
import com.bsuir.distcomp.repository.WriterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WriterService implements CrudService<WriterRequestTo, WriterResponseTo>{

    private final WriterRepository repository;
    private final WriterMapper mapper;
    private final PasswordEncoder passwordEncoder;

    public WriterResponseTo create(WriterRequestTo dto) {
        Optional<Writer> writerOpt = repository.findByLogin(dto.getLogin());
        if(writerOpt.isPresent()){
            throw new EntityAlreadyExistsException("Writer with such login already exists");
        }
        Writer writer = mapper.toEntity(dto);
        writer.setPassword(passwordEncoder.encode(writer.getPassword()));
        Writer saved = repository.saveAndFlush(writer);
        return mapper.toDto(saved);
    }

    public List<WriterResponseTo> getAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    public WriterResponseTo getById(Long id) {
        Writer writer = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Writer not found"));

        return mapper.toDto(writer);
    }

    public WriterResponseTo update(Long id, WriterRequestTo dto) {
        Writer existing = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Writer not found"));

        existing.setLogin(dto.getLogin());
        existing.setPassword(dto.getPassword());
        existing.setFirstname(dto.getFirstname());
        existing.setLastname(dto.getLastname());

        return mapper.toDto(repository.saveAndFlush(existing));
    }

    public void delete(Long id) {
        Writer writer = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Writer not found"));

        repository.delete(writer);
    }
}
