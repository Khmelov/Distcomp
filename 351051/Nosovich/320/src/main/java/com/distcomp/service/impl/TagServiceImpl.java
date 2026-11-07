package com.distcomp.service.impl;

import com.distcomp.dto.filter.TagFilter;
import com.distcomp.dto.request.TagRequestTo;
import com.distcomp.dto.response.TagResponseTo;
import com.distcomp.exception.DuplicateEntityException;
import com.distcomp.exception.EntityNotFoundException;
import com.distcomp.exception.ValidationException;
import com.distcomp.mapper.TagMapper;
import com.distcomp.model.Issue;
import com.distcomp.model.Tag;
import com.distcomp.pagination.PagedResult;
import com.distcomp.repository.IssueRepository;
import com.distcomp.repository.TagRepository;
import com.distcomp.service.TagService;
import com.distcomp.validation.ValidationUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Default {@link TagService} implementation.
 */
@Service
public class TagServiceImpl implements TagService {

    private static final String ENTITY = "Tag";

    private final TagRepository tagRepository;

    private final IssueRepository issueRepository;

    private final TagMapper tagMapper;

    public TagServiceImpl(TagRepository tagRepository,
                          IssueRepository issueRepository,
                          TagMapper tagMapper) {
        this.tagRepository = tagRepository;
        this.issueRepository = issueRepository;
        this.tagMapper = tagMapper;
    }

    @Override
    @Transactional
    public TagResponseTo create(TagRequestTo request) {
        validate(request);
        if (tagRepository.existsByName(request.name())) {
            throw new DuplicateEntityException(ENTITY, "name", request.name());
        }
        return tagMapper.toResponse(tagRepository.save(tagMapper.toEntity(request)));
    }

    @Override
    @Transactional(readOnly = true)
    public TagResponseTo findById(Long id) {
        return tagMapper.toResponse(findEntity(id));
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResult<TagResponseTo> findAll(TagFilter filter, Pageable pageable) {
        Page<Tag> page = tagRepository.findAll(toSpecification(filter), pageable);
        return new PagedResult<>(tagMapper.toResponseList(page.getContent()), page.getTotalElements());
    }

    @Override
    @Transactional
    public TagResponseTo update(Long id, TagRequestTo request) {
        ValidationUtils.requireReference("id", id);
        Tag tag = findEntity(id);
        validate(request);
        tagRepository.findByName(request.name())
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new DuplicateEntityException(ENTITY, "name", request.name());
                });
        tagMapper.updateEntity(request, tag);
        return tagMapper.toResponse(tagRepository.save(tag));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Tag tag = findEntity(id);
        for (Issue issue : issueRepository.findByTagsId(id)) {
            issue.getTags().remove(tag);
            issueRepository.save(issue);
        }
        tagRepository.delete(tag);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TagResponseTo> findByIssueId(Long issueId) {
        ValidationUtils.requireReference("issueId", issueId);
        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() -> new EntityNotFoundException("Issue", issueId));
        return tagMapper.toResponseList(issue.getTags());
    }

    private Tag findEntity(Long id) {
        ValidationUtils.requireReference("id", id);
        return tagRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ENTITY, id));
    }

    private Specification<Tag> toSpecification(TagFilter filter) {
        return (root, query, builder) -> {
            List<jakarta.persistence.criteria.Predicate> predicates = new ArrayList<>();
            if (filter != null && filter.nameContains() != null && !filter.nameContains().isBlank()) {
                predicates.add(builder.like(builder.lower(root.get("name")),
                        "%" + filter.nameContains().toLowerCase() + "%"));
            }
            return builder.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };
    }

    private void validate(TagRequestTo request) {
        if (request == null) {
            throw new ValidationException("Request body must be specified");
        }
        ValidationUtils.requireText("name", request.name(), 2, 32);
    }
}
