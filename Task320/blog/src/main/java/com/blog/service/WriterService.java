package com.blog.service;

import com.blog.dto.WriterRequestTo;
import com.blog.dto.WriterResponseTo;
import com.blog.entity.Writer;
import com.blog.exception.EntityNotFoundException;
import com.blog.mapper.WriterMapper;
import com.blog.repository.ArticleRepository;
import com.blog.repository.WriterRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class WriterService {

    private final WriterRepository writerRepository;
    private final ArticleRepository articleRepository;
    private final WriterMapper writerMapper;

    public WriterService(WriterRepository writerRepository, ArticleRepository articleRepository, WriterMapper writerMapper) {
        this.writerRepository = writerRepository;
        this.articleRepository = articleRepository;
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
        // Check if writer with same login already exists
        writerRepository.findByLogin(request.getLogin()).ifPresent(writer -> {
            throw new IllegalArgumentException("Writer with login '" + request.getLogin() + "' already exists");
        });

        Writer writer = writerMapper.requestToToEntity(request);
        writer.setCreated(java.time.LocalDateTime.now());
        writer.setModified(java.time.LocalDateTime.now());
        Writer savedWriter = writerRepository.save(writer);
        return writerMapper.entityToResponseTo(savedWriter);
    }

    public WriterResponseTo update(Long id, WriterRequestTo request) {
        Writer existingWriter = writerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Writer not found with id: " + id));

        writerMapper.updateEntityFromRequest(request, existingWriter);
        existingWriter.setModified(java.time.LocalDateTime.now());
        Writer updatedWriter = writerRepository.save(existingWriter);
        return writerMapper.entityToResponseTo(updatedWriter);
    }

    public void deleteById(Long id) {
        if (!writerRepository.existsById(id)) {
            throw new EntityNotFoundException("Writer not found with id: " + id);
        }

        // Delete all articles by this writer first
        articleRepository.findByWriter_Id(id).forEach(article -> {
            articleRepository.deleteById(article.getId());
        });

        // Now delete the writer
        writerRepository.deleteById(id);
    }
}