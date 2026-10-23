package com.distcomp.service.impl;

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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Default {@link CreatorService} implementation.
 */
@Service
public class CreatorServiceImpl implements CreatorService {

    private static final String ENTITY = "Creator";

    private final CreatorRepository creatorRepository;

    private final IssueRepository issueRepository;

    private final IssueService issueService;

    private final CreatorMapper creatorMapper;

    public CreatorServiceImpl(CreatorRepository creatorRepository,
                              IssueRepository issueRepository,
                              IssueService issueService,
                              CreatorMapper creatorMapper) {
        this.creatorRepository = creatorRepository;
        this.issueRepository = issueRepository;
        this.issueService = issueService;
        this.creatorMapper = creatorMapper;
    }

    @Override
    @Transactional
    public CreatorResponseTo create(CreatorRequestTo request) {
        validate(request);
        if (creatorRepository.existsByLogin(request.login())) {
            throw new DuplicateEntityException(ENTITY, "login", request.login());
        }
        Creator creator = creatorMapper.toEntity(request);
        return creatorMapper.toResponse(creatorRepository.save(creator));
    }

    @Override
    @Transactional(readOnly = true)
    public CreatorResponseTo findById(Long id) {
        return creatorMapper.toResponse(findEntity(id));
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResult<CreatorResponseTo> findAll(CreatorFilter filter, Pageable pageable) {
        Page<Creator> page = creatorRepository.findAll(toSpecification(filter), pageable);
        return new PagedResult<>(creatorMapper.toResponseList(page.getContent()), page.getTotalElements());
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
        return creatorMapper.toResponse(creatorRepository.save(creator));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Creator creator = findEntity(id);
        for (Issue issue : issueRepository.findByCreatorId(id)) {
            issueService.delete(issue.getId());
        }
        creatorRepository.delete(creator);
    }

    @Override
    @Transactional(readOnly = true)
    public CreatorResponseTo findByIssueId(Long issueId) {
        ValidationUtils.requireReference("issueId", issueId);
        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() -> new EntityNotFoundException("Issue", issueId));
        return creatorMapper.toResponse(issue.getCreator());
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
