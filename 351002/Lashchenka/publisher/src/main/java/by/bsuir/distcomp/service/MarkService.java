package by.bsuir.distcomp.service;

import by.bsuir.distcomp.dto.request.MarkRequestTo;
import by.bsuir.distcomp.dto.response.MarkResponseTo;
import by.bsuir.distcomp.entity.Mark;
import by.bsuir.distcomp.exception.DuplicateException;
import by.bsuir.distcomp.exception.ResourceNotFoundException;
import by.bsuir.distcomp.mapper.MarkMapper;
import by.bsuir.distcomp.repository.MarkRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class MarkService {

    private final MarkRepository markRepository;
    private final MarkMapper markMapper;

    public MarkService(MarkRepository markRepository, MarkMapper markMapper) {
        this.markRepository = markRepository;
        this.markMapper = markMapper;
    }

    @Caching(put = @CachePut(value = "marks", key = "#result.id"),
            evict = @CacheEvict(value = "marks", key = "'all'"))
    public MarkResponseTo create(MarkRequestTo dto) {
        if (markRepository.existsByName(dto.getName())) {
            throw new DuplicateException("Mark with name '" + dto.getName() + "' already exists", 40305);
        }
        Mark entity = markMapper.toEntity(dto);
        Mark saved = markRepository.save(entity);
        return markMapper.toResponseDto(saved);
    }

    @Cacheable(value = "marks", key = "#id")
    @Transactional(readOnly = true)
    public MarkResponseTo getById(Long id) {
        Mark entity = markRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mark with id " + id + " not found", 40409));
        return markMapper.toResponseDto(entity);
    }

    @Cacheable(value = "marks", key = "'all'")
    @Transactional(readOnly = true)
    public List<MarkResponseTo> getAll() {
        return markRepository.findAll().stream()
                .map(markMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Caching(put = @CachePut(value = "marks", key = "#result.id"),
            evict = @CacheEvict(value = "marks", key = "'all'"))
    public MarkResponseTo update(MarkRequestTo dto) {
        Mark existing = markRepository.findById(dto.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Mark with id " + dto.getId() + " not found", 40410));
        if (markRepository.existsByNameAndIdNot(dto.getName(), dto.getId())) {
            throw new DuplicateException("Mark with name '" + dto.getName() + "' already exists", 40306);
        }
        markMapper.updateEntityFromDto(dto, existing);
        Mark updated = markRepository.save(existing);
        return markMapper.toResponseDto(updated);
    }

    @Caching(evict = {
            @CacheEvict(value = "marks", key = "#id"),
            @CacheEvict(value = "marks", key = "'all'")
    })
    public void deleteById(Long id) {
        if (!markRepository.existsById(id)) {
            throw new ResourceNotFoundException("Mark with id " + id + " not found", 40411);
        }
        markRepository.deleteById(id);
    }
}
