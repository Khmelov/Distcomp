package com.example.demo.servises;

import com.example.demo.dto.request.WriterRequestTo;
import com.example.demo.dto.response.WriterResponseTo;
import com.example.demo.exeptionHandler.ConflictException;
import com.example.demo.exeptionHandler.ResourceNotFoundException;
import com.example.demo.mapper.WriterMapper;
import com.example.demo.models.Writer;
import com.example.demo.repository.StoryRepository;
import com.example.demo.repository.WriterRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WriterServise {

    private static final Logger log = LoggerFactory.getLogger(WriterServise.class);

    public final WriterRepository writerRepository;
    public final StoryRepository storyRepository;
    public final WriterMapper writerMapper;

    // Ручной конструктор
    public WriterServise(WriterRepository writerRepository,
                         StoryRepository storyRepository,
                         WriterMapper writerMapper) {
        this.writerRepository = writerRepository;
        this.storyRepository = storyRepository;
        this.writerMapper = writerMapper;
    }

    public WriterResponseTo create(WriterRequestTo request) {
        log.debug("Creating new writer: {}", request);
        if (writerRepository.existsByLogin(request.getLogin())) {
            throw new ConflictException("Writer with this login already exists");
        }
        Writer writer = writerMapper.requestToEntity(request);
        Writer savedWriter = writerRepository.save(writer);
        return writerMapper.toResponse(savedWriter);
    }

    public List<WriterResponseTo> findAll(int page, int size, String sortBy, String sortDir, String login) {
        log.debug("Finding all writers");
        Sort sort = "desc".equalsIgnoreCase(sortDir) ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Specification<Writer> spec = (root, query, cb) ->
                login == null || login.isBlank() ? cb.conjunction() : cb.like(cb.lower(root.get("login")), "%" + login.toLowerCase() + "%");
        return writerMapper.toResponseList(writerRepository.findAll(spec, PageRequest.of(page, size, sort)).getContent());
    }

    public void delete(Long id) {
        log.debug("Deleting writer with id: {}", id);
        if (!writerRepository.existsById(id)) {
            throw new ResourceNotFoundException("Writer", id);
        }
        if (storyRepository.existsByWriterId(id)) {
            throw new ConflictException("Writer cannot be deleted because it is referenced by stories");
        }
        writerRepository.deleteById(id);
    }

    public WriterResponseTo findById(Long id) {
        log.debug("Finding writer by id: {}", id);
        Writer writer = writerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Writer", id));
        return writerMapper.toResponse(writer);
    }

    public WriterResponseTo update(Long id, WriterRequestTo writerRequestTo) {
        log.debug("Updating writer with id: {} with data: {}", id, writerRequestTo);
        Writer existingWriter = writerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Writer", id));
        if (writerRepository.existsByLoginAndIdNot(writerRequestTo.getLogin(), id)) {
            throw new ConflictException("Writer with this login already exists");
        }
        Writer writer = writerMapper.requestToEntity(writerRequestTo);
        writer.setId(existingWriter.getId());
        return writerMapper.toResponse(writerRepository.save(writer));
    }
}