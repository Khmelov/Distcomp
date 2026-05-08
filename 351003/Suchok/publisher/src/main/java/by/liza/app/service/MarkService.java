package by.liza.app.service;

import by.liza.app.dto.request.MarkRequestTo;
import by.liza.app.dto.response.MarkResponseTo;
import by.liza.app.exception.DuplicateEntityException;
import by.liza.app.exception.EntityNotFoundException;
import by.liza.app.mapper.MarkMapper;
import by.liza.app.model.Mark;
import by.liza.app.repository.ArticleRepository;
import by.liza.app.repository.MarkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MarkService {

    private final MarkRepository    markRepository;
    private final ArticleRepository articleRepository;
    private final MarkMapper        markMapper;

    @Transactional
    @Caching(
            put  = @CachePut(value = "marks", key = "#result.id"),
            evict = @CacheEvict(value = "marks_all", allEntries = true)
    )
    public MarkResponseTo create(MarkRequestTo requestTo) {
        if (markRepository.existsByName(requestTo.getName())) {
            throw new DuplicateEntityException(
                    "Mark with name '" + requestTo.getName() + "' already exists", 40303);
        }
        Mark mark = markMapper.toEntity(requestTo);
        return markMapper.toResponse(markRepository.save(mark));
    }

    @Cacheable(value = "marks", key = "#id")
    public MarkResponseTo getById(Long id) {
        return markMapper.toResponse(findById(id));
    }

    @Cacheable(value = "marks_all")
    public List<MarkResponseTo> getAll() {
        return markMapper.toResponseList(markRepository.findAll());
    }

    @Transactional
    @Caching(
            put  = @CachePut(value = "marks", key = "#result.id"),
            evict = @CacheEvict(value = "marks_all", allEntries = true)
    )
    public MarkResponseTo update(MarkRequestTo requestTo) {
        if (requestTo.getId() == null) {
            throw new EntityNotFoundException("Mark id must be provided for update", 40003);
        }
        Mark existing = findById(requestTo.getId());
        if (markRepository.existsByNameAndIdNot(requestTo.getName(), requestTo.getId())) {
            throw new DuplicateEntityException(
                    "Mark with name '" + requestTo.getName() + "' already exists", 40303);
        }
        existing.setName(requestTo.getName());
        return markMapper.toResponse(markRepository.save(existing));
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "marks",     key = "#id"),
            @CacheEvict(value = "marks_all", allEntries = true)
    })
    public void delete(Long id) {
        if (!markRepository.existsById(id)) {
            throw new EntityNotFoundException("Mark with id " + id + " not found", 40403);
        }
        markRepository.deleteById(id);
    }

    public List<MarkResponseTo> getByArticleId(Long articleId) {
        if (!articleRepository.existsById(articleId)) {
            throw new EntityNotFoundException("Article with id " + articleId + " not found", 40402);
        }
        return markMapper.toResponseList(markRepository.findByArticleId(articleId));
    }

    private Mark findById(Long id) {
        return markRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Mark with id " + id + " not found", 40403));
    }
}