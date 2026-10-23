package com.distcomp.service.impl;

import com.distcomp.dto.filter.IssueFilter;
import com.distcomp.dto.request.IssueRequestTo;
import com.distcomp.dto.response.IssueResponseTo;
import com.distcomp.exception.DuplicateEntityException;
import com.distcomp.exception.EntityNotFoundException;
import com.distcomp.exception.ValidationException;
import com.distcomp.mapper.IssueMapper;
import com.distcomp.model.Creator;
import com.distcomp.model.Issue;
import com.distcomp.model.Tag;
import com.distcomp.pagination.PagedResult;
import com.distcomp.repository.CreatorRepository;
import com.distcomp.repository.IssueRepository;
import com.distcomp.repository.NoticeRepository;
import com.distcomp.repository.TagRepository;
import com.distcomp.service.IssueService;
import com.distcomp.validation.ValidationUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Default {@link IssueService} implementation.
 *
 * <p>Owns the Issue-Creator and Issue-Tag associations, the life-cycle
 * timestamps and the removal of tags that are no longer referenced.</p>
 */
@Service
public class IssueServiceImpl implements IssueService {

    private static final String ENTITY = "Issue";

    private final IssueRepository issueRepository;

    private final CreatorRepository creatorRepository;

    private final TagRepository tagRepository;

    private final NoticeRepository noticeRepository;

    private final IssueMapper issueMapper;

    public IssueServiceImpl(IssueRepository issueRepository,
                            CreatorRepository creatorRepository,
                            TagRepository tagRepository,
                            NoticeRepository noticeRepository,
                            IssueMapper issueMapper) {
        this.issueRepository = issueRepository;
        this.creatorRepository = creatorRepository;
        this.tagRepository = tagRepository;
        this.noticeRepository = noticeRepository;
        this.issueMapper = issueMapper;
    }

    @Override
    @Transactional
    public IssueResponseTo create(IssueRequestTo request) {
        validate(request);
        Creator creator = requireCreator(request.creatorId());
        if (issueRepository.existsByTitle(request.title())) {
            throw new DuplicateEntityException(ENTITY, "title", request.title());
        }

        Issue issue = issueMapper.toEntity(request);
        issue.setCreator(creator);
        Instant now = Instant.now();
        issue.setCreated(now);
        issue.setModified(now);
        issue.setTags(resolveTags(request.tags()));
        return issueMapper.toResponse(issueRepository.save(issue));
    }

    @Override
    @Transactional(readOnly = true)
    public IssueResponseTo findById(Long id) {
        return issueMapper.toResponse(findEntity(id));
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResult<IssueResponseTo> findAll(IssueFilter filter, Pageable pageable) {
        Page<Issue> page = issueRepository.findAll(toSpecification(filter), pageable);
        return new PagedResult<>(issueMapper.toResponseList(page.getContent()), page.getTotalElements());
    }

    @Override
    @Transactional
    public IssueResponseTo update(Long id, IssueRequestTo request) {
        ValidationUtils.requireReference("id", id);
        Issue issue = findEntity(id);
        validate(request);
        Creator creator = requireCreator(request.creatorId());
        issueRepository.findByTitle(request.title())
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new DuplicateEntityException(ENTITY, "title", request.title());
                });

        issueMapper.updateEntity(request, issue);
        issue.setCreator(creator);
        if (request.tags() != null) {
            Set<Tag> tags = resolveTags(request.tags());
            issue.getTags().clear();
            issue.getTags().addAll(tags);
        }
        issue.setModified(Instant.now());
        return issueMapper.toResponse(issueRepository.save(issue));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Issue issue = findEntity(id);
        Set<Tag> affectedTags = new LinkedHashSet<>(issue.getTags());

        noticeRepository.deleteByIssueId(id);
        issue.getTags().clear();
        issueRepository.delete(issue);
        issueRepository.flush();

        for (Tag tag : affectedTags) {
            if (issueRepository.countByTagsId(tag.getId()) == 0) {
                tagRepository.delete(tag);
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<IssueResponseTo> findByCreatorId(Long creatorId) {
        ValidationUtils.requireReference("creatorId", creatorId);
        return issueMapper.toResponseList(issueRepository.findByCreatorId(creatorId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<IssueResponseTo> findByTagId(Long tagId) {
        ValidationUtils.requireReference("tagId", tagId);
        return issueMapper.toResponseList(issueRepository.findByTagsId(tagId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<IssueResponseTo> findByTagName(String tagName) {
        ValidationUtils.requireText("tagName", tagName, 2, 32);
        return issueMapper.toResponseList(
                issueRepository.findAll(toSpecification(new IssueFilter(null, null, tagName))));
    }

    private Issue findEntity(Long id) {
        ValidationUtils.requireReference("id", id);
        return issueRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ENTITY, id));
    }

    private Creator requireCreator(Long creatorId) {
        ValidationUtils.requireReference("creatorId", creatorId);
        return creatorRepository.findById(creatorId)
                .orElseThrow(() -> new EntityNotFoundException("Creator", creatorId));
    }

    /**
     * Resolves tag names to entities: an existing tag is reused, a missing one
     * is created.
     */
    private Set<Tag> resolveTags(List<String> tagNames) {
        Set<Tag> tags = new LinkedHashSet<>();
        if (tagNames == null) {
            return tags;
        }
        for (String name : tagNames) {
            ValidationUtils.requireText("tag name", name, 2, 32);
            Tag tag = tagRepository.findByName(name)
                    .orElseGet(() -> {
                        Tag created = new Tag();
                        created.setName(name);
                        return tagRepository.save(created);
                    });
            tags.add(tag);
        }
        return tags;
    }

    private Specification<Issue> toSpecification(IssueFilter filter) {
        return (root, query, builder) -> {
            List<jakarta.persistence.criteria.Predicate> predicates = new ArrayList<>();
            if (filter == null) {
                return builder.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
            }
            if (filter.creatorId() != null) {
                predicates.add(builder.equal(root.get("creator").get("id"), filter.creatorId()));
            }
            if (filter.titleContains() != null && !filter.titleContains().isBlank()) {
                predicates.add(builder.like(builder.lower(root.get("title")),
                        "%" + filter.titleContains().toLowerCase() + "%"));
            }
            if (filter.tagName() != null && !filter.tagName().isBlank()) {
                var subquery = query.subquery(Long.class);
                var taggedIssue = subquery.from(Issue.class);
                var tags = taggedIssue.join("tags");
                subquery.select(taggedIssue.get("id"))
                        .where(builder.equal(builder.lower(tags.get("name")),
                                filter.tagName().toLowerCase()));
                predicates.add(root.get("id").in(subquery));
            }
            return builder.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };
    }

    private void validate(IssueRequestTo request) {
        if (request == null) {
            throw new ValidationException("Request body must be specified");
        }
        ValidationUtils.requireReference("creatorId", request.creatorId());
        ValidationUtils.requireText("title", request.title(), 2, 64);
        ValidationUtils.requireText("content", request.content(), 4, 2048);
    }
}
