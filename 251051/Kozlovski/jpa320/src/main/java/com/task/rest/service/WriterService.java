package com.task.rest.service;


import com.task.rest.dto.WriterRequestTo;
import com.task.rest.dto.WriterResponseTo;
import com.task.rest.exception.ConflictException;
import com.task.rest.model.Writer;
import com.task.rest.repository.WriterRepository;
import com.task.rest.util.WriterMapper;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

import java.util.ConcurrentModificationException;
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

    public WriterResponseTo createWriter(@Valid WriterRequestTo requestTo) {
        Writer writer = writerMapper.toEntity(requestTo);
        if(writerRepository.existsByLogin(requestTo.getLogin())){
            throw new ConflictException("Writer with login '" + requestTo.getLogin() + "' already exists");
        }
        Writer savedWriter = writerRepository.save(writer);
        return writerMapper.toResponse(savedWriter);
    }

    public WriterResponseTo getWriterById(Long id) {
        Writer writer = writerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Writer not found with id: " + id));
        return writerMapper.toResponse(writer);
    }

    public List<WriterResponseTo> getAllWriters() {
        return writerRepository.findAll().stream()
                .map(writerMapper::toResponse)
                .collect(Collectors.toList());
    }

    public WriterResponseTo updateWriter(Long id, @Valid WriterRequestTo requestTo) {
        Writer existingWriter = writerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Writer not found with id: " + id));
        writerMapper.updateEntityFromDto(requestTo, existingWriter);
        Writer updatedWriter = writerRepository.save(existingWriter);
        return writerMapper.toResponse(updatedWriter);
    }

    public void deleteWriter(Long id) {
        if (!writerRepository.existsById(id)) {
            throw new RuntimeException("Writer not found with id: " + id);
        }
        writerRepository.deleteById(id);
    }

    public WriterResponseTo getWriterByTweetId(Long tweetId) {
        return writerRepository.findAll().stream()
                .filter(writer -> writerRepository.findById(writer.getId()).stream()
                        .anyMatch(tweet -> tweet.getId().equals(tweetId)))
                .findFirst()
                .map(writerMapper::toResponse)
                .orElseThrow(() -> new RuntimeException("Writer not found for tweet with id: " + tweetId));
    }
}
