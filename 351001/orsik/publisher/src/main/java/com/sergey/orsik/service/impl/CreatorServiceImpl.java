package com.sergey.orsik.service.impl;

import com.sergey.orsik.dto.request.CreatorRequestTo;
import com.sergey.orsik.dto.response.CreatorResponseTo;
import com.sergey.orsik.entity.Creator;
import com.sergey.orsik.entity.CreatorRole;
import com.sergey.orsik.exception.ConflictException;
import com.sergey.orsik.exception.EntityNotFoundException;
import com.sergey.orsik.mapper.CreatorMapper;
import com.sergey.orsik.repository.CreatorRepository;
import com.sergey.orsik.service.CreatorService;
import jakarta.transaction.Transactional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@Transactional
public class CreatorServiceImpl implements CreatorService {

    private final CreatorRepository repository;
    private final CreatorMapper mapper;
    private final PasswordEncoder passwordEncoder;

    public CreatorServiceImpl(CreatorRepository repository, CreatorMapper mapper, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.mapper = mapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Cacheable(
            value = "creators:list",
            key = "T(java.util.Objects).hash(#page, #size, #sortBy, #sortDir, #search)")
    public List<CreatorResponseTo> findAll(int page, int size, String sortBy, String sortDir, String search) {
        Pageable pageable = PageRequest.of(page, size, buildSort(sortBy, sortDir));
        Specification<Creator> spec = (root, query, cb) -> cb.conjunction();
        if (StringUtils.hasText(search)) {
            String pattern = "%" + search.toLowerCase() + "%";
            spec = spec.and((root, query, cb) -> cb.or(
                    cb.like(cb.lower(root.get("login")), pattern),
                    cb.like(cb.lower(root.get("firstname")), pattern),
                    cb.like(cb.lower(root.get("lastname")), pattern)
            ));
        }
        return repository.findAll(spec, pageable).stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    @Cacheable(value = "creators", key = "#id")
    public CreatorResponseTo findById(Long id) {
        Creator entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Creator", id));
        return mapper.toResponse(entity);
    }

    @Override
    @CacheEvict(value = "creators:list", allEntries = true)
    public CreatorResponseTo create(CreatorRequestTo request) {
        if (repository.existsByLogin(request.getLogin())) {
            throw new ConflictException("Creator with login '%s' already exists".formatted(request.getLogin()));
        }
        Creator entity = mapper.toEntity(request);
        entity.setId(null);
        entity.setPassword(passwordEncoder.encode(request.getPassword()));
        if (entity.getRole() == null) {
            entity.setRole(CreatorRole.CUSTOMER);
        }
        Creator saved = repository.save(entity);
        return mapper.toResponse(saved);
    }

    @Override
    @Caching(
            evict = {
                @CacheEvict(value = "creators", key = "#id"),
                @CacheEvict(value = "creators:list", allEntries = true)
            })
    public CreatorResponseTo update(Long id, CreatorRequestTo request) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Creator", id);
        }
        if (repository.existsByLoginAndIdNot(request.getLogin(), id)) {
            throw new ConflictException("Creator with login '%s' already exists".formatted(request.getLogin()));
        }
        Creator entity = mapper.toEntity(request);
        entity.setId(id);
        entity.setPassword(passwordEncoder.encode(request.getPassword()));
        if (entity.getRole() == null) {
            entity.setRole(CreatorRole.CUSTOMER);
        }
        Creator saved = repository.save(entity);
        return mapper.toResponse(saved);
    }

    @Override
    @Caching(
            evict = {
                @CacheEvict(value = "creators", key = "#id"),
                @CacheEvict(value = "creators:list", allEntries = true)
            })
    public void deleteById(Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Creator", id);
        }
        repository.deleteById(id);
    }

    private Sort buildSort(String sortBy, String sortDir) {
        String targetField = StringUtils.hasText(sortBy) ? sortBy : "id";
        Sort.Direction direction = "desc".equalsIgnoreCase(sortDir) ? Sort.Direction.DESC : Sort.Direction.ASC;
        return Sort.by(direction, targetField);
    }
}
