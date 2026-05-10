package by.bsuir.task310.service;

import by.bsuir.task310.dto.LabelRequestTo;
import by.bsuir.task310.dto.LabelResponseTo;
import by.bsuir.task310.exception.EntityNotFoundException;
import by.bsuir.task310.mapper.LabelMapper;
import by.bsuir.task310.model.Label;
import by.bsuir.task310.repository.LabelRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LabelService {

    private final LabelRepository repository;
    private final LabelMapper mapper;

    public LabelService(LabelRepository repository, LabelMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @CachePut(value = "labels", key = "#result.id")
    public LabelResponseTo create(LabelRequestTo requestTo) {
        Label label = mapper.toEntity(requestTo);
        Label saved = repository.save(label);
        return mapper.toResponseTo(saved);
    }

    public List<LabelResponseTo> getAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toResponseTo)
                .toList();
    }

    @Cacheable(value = "labels", key = "#id")
    public LabelResponseTo getById(Long id) {
        Label label = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Label not found"));
        return mapper.toResponseTo(label);
    }

    @CachePut(value = "labels", key = "#requestTo.id")
    public LabelResponseTo update(LabelRequestTo requestTo) {
        if (!repository.existsById(requestTo.getId())) {
            throw new EntityNotFoundException("Label not found");
        }

        Label label = mapper.toEntity(requestTo);
        Label updated = repository.save(label);
        return mapper.toResponseTo(updated);
    }

    @CacheEvict(value = "labels", key = "#id")
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Label not found");
        }

        repository.deleteById(id);
    }
}