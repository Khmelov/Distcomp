package com.distcomp.service.impl;

import com.distcomp.cache.CacheService;
import com.distcomp.dto.filter.CreatorFilter;
import com.distcomp.dto.request.CreatorRequestTo;
import com.distcomp.dto.response.CreatorResponseTo;
import com.distcomp.exception.DuplicateEntityException;
import com.distcomp.exception.EntityNotFoundException;
import com.distcomp.exception.ValidationException;
import com.distcomp.mapper.CreatorMapper;
import com.distcomp.model.Creator;
import com.distcomp.model.Issue;
import com.distcomp.pagination.PagedResult;
import com.distcomp.repository.CreatorRepository;
import com.distcomp.repository.IssueRepository;
import com.distcomp.service.CreatorService;
import com.distcomp.service.IssueService;
import com.distcomp.validation.ValidationUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Default {@link CreatorService} implementation.
 *
 * <p>Reads are cached in Redis with a short time-to-live. Writes invalidate the
 * whole creator namespace, which keeps the cache consistent without tracking
 * every list query separately.</p>
 */
@Service
public class CreatorServiceImpl implements CreatorService {

    private static final String ENTITY = "Creator";

    private static final String CACHE_PREFIX = "distcomp:creator:";

    private final CreatorRepository creatorRepository;

    private final IssueRepository issueRepository;

    private final IssueService issueService;

    private final CreatorMapper creatorMapper;

    private final CacheService cache;

    public CreatorServiceImpl(CreatorRepository creatorRepository,
                              IssueRepository issueRepository,
                              IssueService issueService,
                              CreatorMapper creatorMapper,
                              CacheService cache) {
        this.creatorRepository = creatorRepository;
        this.issueRepository = issueRepository;
        this.issueService = issueService;
        this.creatorMapper = creatorMapper;
        this.cache = cache;
    }

    @Override
    @Transactional
    public CreatorResponseTo create(CreatorRequestTo request) {
        validate(request);
        if (creatorRepository.existsByLogin(request.login())) {
            throw new DuplicateEntityException(ENTITY, "login", request.login());
        }
        Creator creator = creatorMapper.toEntity(request);
        CreatorResponseTo response = creatorMapper.toResponse(creatorRepository.save(creator));
        cache.evictByPrefix(CACHE_PREFIX);
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public CreatorResponseTo findById(Long id) {
        String key = CACHE_PREFIX + id;
        CreatorResponseTo cached = cache.get(key, new TypeReference<CreatorResponseTo>() {
        });
        if (cached != null) {
            return cached;
        }
        CreatorResponseTo response = creatorMapper.toResponse(findEntity(id));
        cache.put(key, response);
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResult<CreatorResponseTo> findAll(CreatorFilter filter, Pageable pageable) {
        String key = listKey(filter, pageable);
        PagedResult<CreatorResponseTo> cached = cache.get(key,
                new TypeReference<PagedResult<CreatorResponseTo>>() {
                });
        if (cached != null) {
            return cached;
        }
        Page<Creator> page = creatorRepository.findAll(toSpecification(filter), pageable);
        PagedResult<CreatorResponseTo> result =
                new PagedResult<>(creatorMapper.toResponseList(page.getContent()), page.getTotalElements());
        cache.put(key, result);
        return result;
    }

    @Override
    @Transactional
    public CreatorResponseTo update(Long id, CreatorRequestTo request) {
        ValidationUtils.requireReference("id", id);
        Creator creator = findEntity(id);
        validate(request);
        creatorRepository.findByLogin(request.login())
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new DuplicateEntityException(ENTITY, "login", request.login());
                });
        creatorMapper.updateEntity(request, creator);
        CreatorResponseTo response = creatorMapper.toResponse(creatorRepository.save(creator));
        cache.evictByPrefix(CACHE_PREFIX);
        return response;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Creator creator = findEntity(id);
        for (Issue issue : issueRepository.findByCreatorId(id)) {
            issueService.delete(issue.getId());
        }
        creatorRepository.delete(creator);
        cache.evictByPrefix(CACHE_PREFIX);
    }

    @Override
    @Transactional(readOnly = true)
    public CreatorResponseTo findByIssueId(Long issueId) {
        ValidationUtils.requireReference("issueId", issueId);
        String key = CACHE_PREFIX + "by-issue:" + issueId;
        CreatorResponseTo cached = cache.get(key, new TypeReference<CreatorResponseTo>() {
        });
        if (cached != null) {
            return cached;
        }
        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() -> new EntityNotFoundException("Issue", issueId));
        CreatorResponseTo response = creatorMapper.toResponse(issue.getCreator());
        cache.put(key, response);
        return response;
    }

    private Creator findEntity(Long id) {
        ValidationUtils.requireReference("id", id);
        return creatorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ENTITY, id));
    }

    private Specification<Creator> toSpecification(CreatorFilter filter) {
        return (root, query, builder) -> {
            List<jakarta.persistence.criteria.Predicate> predicates = new ArrayList<>();
            if (filter != null && filter.login() != null && !filter.login().isBlank()) {
                predicates.add(builder.like(builder.lower(root.get("login")),
                        "%" + filter.login().toLowerCase() + "%"));
            }
            return builder.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };
    }

    private String listKey(CreatorFilter filter, Pageable pageable) {
        String login = filter == null ? null : filter.login();
        String paging = pageable.isPaged()
                ? pageable.getPageNumber() + ":" + pageable.getPageSize()
                : "all";
        String sort = pageable.getSort().isSorted() ? pageable.getSort().toString() : "id: ASC";
        return CACHE_PREFIX + "list:" + login + ":" + paging + ":" + sort;
    }

    private void validate(CreatorRequestTo request) {
        if (request == null) {
            throw new ValidationException("Request body must be specified");
        }
        ValidationUtils.requireText("login", request.login(), 2, 64);
        ValidationUtils.requireText("password", request.password(), 8, 128);
        ValidationUtils.requireText("firstname", request.firstname(), 2, 64);
        ValidationUtils.requireText("lastname", request.lastname(), 2, 64);
    }
}
