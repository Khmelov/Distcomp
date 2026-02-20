package by.bsuir.distcomp.service;

import by.bsuir.distcomp.dto.request.MarkRequestTo;
import by.bsuir.distcomp.dto.response.MarkResponseTo;
import by.bsuir.distcomp.entity.Mark;
import by.bsuir.distcomp.exception.DuplicateException;
import by.bsuir.distcomp.exception.ResourceNotFoundException;
import by.bsuir.distcomp.mapper.MarkMapper;
import by.bsuir.distcomp.repository.impl.InMemoryMarkRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MarkService {

    private final InMemoryMarkRepository markRepository;
    private final MarkMapper markMapper;

    public MarkService(InMemoryMarkRepository markRepository, MarkMapper markMapper) {
        this.markRepository = markRepository;
        this.markMapper = markMapper;
    }

    public MarkResponseTo create(MarkRequestTo dto) {
        if (markRepository.existsByName(dto.getName())) {
            throw new DuplicateException("Mark with name '" + dto.getName() + "' already exists", 40305);
        }
        Mark entity = markMapper.toEntity(dto);
        Mark saved = markRepository.save(entity);
        return markMapper.toResponseDto(saved);
    }

    public MarkResponseTo getById(Long id) {
        Mark entity = markRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mark with id " + id + " not found", 40409));
        return markMapper.toResponseDto(entity);
    }

    public List<MarkResponseTo> getAll() {
        return markRepository.findAll().stream()
                .map(markMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    public MarkResponseTo update(MarkRequestTo dto) {
        if (!markRepository.existsById(dto.getId())) {
            throw new ResourceNotFoundException("Mark with id " + dto.getId() + " not found", 40410);
        }
        if (markRepository.existsByNameAndIdNot(dto.getName(), dto.getId())) {
            throw new DuplicateException("Mark with name '" + dto.getName() + "' already exists", 40306);
        }
        Mark entity = markMapper.toEntity(dto);
        Mark updated = markRepository.update(entity);
        return markMapper.toResponseDto(updated);
    }

    public void deleteById(Long id) {
        if (!markRepository.existsById(id)) {
            throw new ResourceNotFoundException("Mark with id " + id + " not found", 40411);
        }
        markRepository.deleteById(id);
    }
}
