package org.example.task310rest.service.impl;

import java.util.List;
import java.util.stream.Collectors;
import org.example.task310rest.dto.WriterRequestTo;
import org.example.task310rest.dto.WriterResponseTo;
import org.example.task310rest.exception.NotFoundException;
import org.example.task310rest.mapper.WriterMapper;
import org.example.task310rest.model.Writer;
import org.example.task310rest.repository.WriterRepository;
import org.example.task310rest.service.WriterService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class WriterServiceImpl implements WriterService {

    private final WriterRepository repository;
    private final WriterMapper mapper;

    public WriterServiceImpl(WriterRepository repository, WriterMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public WriterResponseTo create(WriterRequestTo request) {
        validate(request);
        Writer entity = mapper.toEntity(request);
        repository.save(entity);
        return mapper.toDto(entity);
    }

    @Override
    public WriterResponseTo getById(Long id) {
        Writer entity = repository.findById(id).orElseThrow(() -> new NotFoundException("Writer not found: " + id));
        return mapper.toDto(entity);
    }

    @Override
    public List<WriterResponseTo> getAll() {
        return repository.findAll().stream().map(mapper::toDto).collect(Collectors.toList());
    }

    @Override
    public WriterResponseTo update(Long id, WriterRequestTo request) {
        validate(request);
        Writer entity = repository.findById(id).orElseThrow(() -> new NotFoundException("Writer not found: " + id));
        mapper.updateEntityFromDto(request, entity);
        repository.save(entity);
        return mapper.toDto(entity);
    }

    @Override
    public void delete(Long id) {
        Writer entity = repository.findById(id).orElseThrow(() -> new NotFoundException("Writer not found: " + id));
        repository.deleteById(entity.getId());
    }

    private void validate(WriterRequestTo request) {
        if (!StringUtils.hasText(request.getLogin())
                || !StringUtils.hasText(request.getPassword())
                || !StringUtils.hasText(request.getFirstname())
                || !StringUtils.hasText(request.getLastname())) {
            throw new org.example.task310rest.exception.ValidationException("Writer fields must not be blank");
        }
    }
}


