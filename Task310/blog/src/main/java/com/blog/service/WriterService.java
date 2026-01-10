package com.blog.service;

import com.blog.dto.WriterRequestTo;
import com.blog.dto.WriterResponseTo;
import com.blog.entity.Writer;
import com.blog.exception.EntityNotFoundException;
import com.blog.mapper.WriterMapper;
import com.blog.repository.WriterRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class WriterService {

    private final WriterRepository writerRepository;
    private final WriterMapper writerMapper;

    public WriterService(WriterRepository writerRepository, WriterMapper writerMapper) {
        this.writerRepository = writerRepository;
        this.writerMapper = writerMapper;
    }

    public List<WriterResponseTo> findAll() {
        return writerRepository.findAll().stream()
                .map(writerMapper::entityToResponseTo)
                .collect(Collectors.toList());
    }

    public WriterResponseTo findById(Long id) {
        Writer writer = writerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Writer not found with id: " + id));
        return writerMapper.entityToResponseTo(writer);
    }

    public WriterResponseTo create(WriterRequestTo request) {
        Writer writer = writerMapper.requestToToEntity(request);
        Writer savedWriter = writerRepository.save(writer);
        return writerMapper.entityToResponseTo(savedWriter);
    }

    public WriterResponseTo update(Long id, WriterRequestTo request) {
        Writer existingWriter = writerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Writer not found with id: " + id));

        writerMapper.updateEntityFromRequest(request, existingWriter);
        Writer updatedWriter = writerRepository.save(existingWriter);
        return writerMapper.entityToResponseTo(updatedWriter);
    }

    public void deleteById(Long id) {
        if (!writerRepository.existsById(id)) {
            throw new EntityNotFoundException("Writer not found with id: " + id);
        }
        writerRepository.deleteById(id);
    }
}