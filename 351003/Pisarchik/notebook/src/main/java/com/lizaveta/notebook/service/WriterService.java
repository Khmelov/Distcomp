package com.lizaveta.notebook.service;

import com.lizaveta.notebook.exception.ResourceNotFoundException;
import com.lizaveta.notebook.mapper.WriterMapper;
import com.lizaveta.notebook.model.dto.request.WriterRequestTo;
import com.lizaveta.notebook.model.dto.response.WriterResponseTo;
import com.lizaveta.notebook.model.entity.Story;
import com.lizaveta.notebook.model.entity.Writer;
import com.lizaveta.notebook.repository.StoryRepository;
import com.lizaveta.notebook.repository.WriterRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service for Writer CRUD operations.
 */
@Service
public class WriterService {

    private static final String WRITER_NOT_FOUND = "Writer not found with id: ";

    private final WriterRepository repository;
    private final StoryRepository storyRepository;
    private final WriterMapper mapper;

    public WriterService(
            final WriterRepository repository,
            final StoryRepository storyRepository,
            final WriterMapper mapper) {
        this.repository = repository;
        this.storyRepository = storyRepository;
        this.mapper = mapper;
    }

    public WriterResponseTo create(final WriterRequestTo request) {
        Writer entity = mapper.toEntity(request);
        Writer saved = repository.save(entity);
        return mapper.toResponse(saved);
    }

    public List<WriterResponseTo> findAll() {
        return repository.findAll().stream()
                .map(mapper::toResponse)
                .toList();
    }

    public WriterResponseTo findById(final Long id) {
        Writer entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(WRITER_NOT_FOUND + id));
        return mapper.toResponse(entity);
    }

    public WriterResponseTo findByStoryId(final Long storyId) {
        Long writerId = storyRepository.findById(storyId)
                .map(Story::getWriterId)
                .orElseThrow(() -> new ResourceNotFoundException("Story not found with id: " + storyId));
        return findById(writerId);
    }

    public WriterResponseTo update(final Long id, final WriterRequestTo request) {
        Writer existing = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(WRITER_NOT_FOUND + id));
        Writer updated = existing.withLogin(request.login())
                .withPassword(request.password())
                .withFirstname(request.firstname())
                .withLastname(request.lastname());
        repository.update(updated);
        return mapper.toResponse(updated);
    }

    public void deleteById(final Long id) {
        boolean deleted = repository.deleteById(id);
        if (!deleted) {
            throw new ResourceNotFoundException(WRITER_NOT_FOUND + id);
        }
    }
}
