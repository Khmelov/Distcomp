package com.distcomp.service.impl;

import com.distcomp.cache.CacheService;
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
import com.distcomp.repository.TagRepository;
import com.distcomp.service.IssueService;
import com.distcomp.service.NoticeService;
import com.distcomp.validation.ValidationUtils;
import com.fasterxml.jackson.core.type.TypeReference;
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
 * <p>Issues remain in PostgreSQL. The only change caused by the modularisation
 * is that the notices of an issue are deleted through {@link NoticeService},
 * because their rows now live in Cassandra.</p>
 */
@Service
public class IssueServiceImpl implements IssueService {

    private static final String ENTITY = "Issue";

    private static final String CACHE_PREFIX = "distcomp:issue:";

    private static final String TAG_CACHE_PREFIX = "distcomp:tag:";

    private final IssueRepository issueRepository;

    private final CreatorRepository creatorRepository;

    private final TagRepository tagRepository;

    private final NoticeService noticeService;

    private final IssueMapper issueMapper;

    private final CacheService cache;

    public IssueServiceImpl(IssueRepository issueRepository,
                            CreatorRepository creatorRepository,
                            TagRepository tagRepository,
                            NoticeService noticeService,
                            IssueMapper issueMapper,
                            CacheService cache) {
        this.issueRepository = issueRepository;
        this.creatorRepository = creatorRepository;
        this.tagRepository = tagRepository;
        this.noticeService = noticeService;
        this.issueMapper = issueMapper;
        this.cache = cache;
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
        IssueResponseTo response = issueMapper.toResponse(issueRepository.save(issue));
        evictLists();
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public IssueResponseTo findById(Long id) {
        String key = CACHE_PREFIX + id;
        IssueResponseTo cached = cache.get(key, new TypeReference<IssueResponseTo>() {
        });
        if (cached != null) {
            return cached;
        }
        IssueResponseTo response = issueMapper.toResponse(findEntity(id));
        cache.put(key, response);
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResult<IssueResponseTo> findAll(IssueFilter filter, Pageable pageable) {
        String key = listKey(filter, pageable);
        PagedResult<IssueResponseTo> cached = cache.get(key,
                new TypeReference<PagedResult<IssueResponseTo>>() {
                });
        if (cached != null) {
            return cached;
        }
        Page<Issue> page = issueRepository.findAll(toSpecification(filter), pageable);
        PagedResult<IssueResponseTo> result =
                new PagedResult<>(issueMapper.toResponseList(page.getContent()), page.getTotalElements());
        cache.put(key, result);
        return result;
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
        IssueResponseTo response = issueMapper.toResponse(issueRepository.save(issue));
        evictLists();
        return response;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Issue issue = findEntity(id);
        Set<Tag> affectedTags = new LinkedHashSet<>(issue.getTags());

        noticeService.deleteByIssueId(id);
        issue.getTags().clear();
        issueRepository.delete(issue);
        issueRepository.flush();

        for (Tag tag : affectedTags) {
            if (issueRepository.countByTagsId(tag.getId()) == 0) {
                tagRepository.delete(tag);
            }
        }
        evictLists();
        cache.evictByPrefix("distcomp:notice:");
    }

    @Override
    @Transactional(readOnly = true)
    public List<IssueResponseTo> findByCreatorId(Long creatorId) {
        ValidationUtils.requireReference("creatorId", creatorId);
        String key = CACHE_PREFIX + "by-creator:" + creatorId;
        List<IssueResponseTo> cached = cache.get(key, new TypeReference<List<IssueResponseTo>>() {
        });
        if (cached != null) {
            return cached;
        }
        List<IssueResponseTo> response = issueMapper.toResponseList(issueRepository.findByCreatorId(creatorId));
        cache.put(key, response);
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public List<IssueResponseTo> findByTagId(Long tagId) {
        ValidationUtils.requireReference("tagId", tagId);
        String key = CACHE_PREFIX + "by-tag-id:" + tagId;
        List<IssueResponseTo> cached = cache.get(key, new TypeReference<List<IssueResponseTo>>() {
        });
        if (cached != null) {
            return cached;
        }
        List<IssueResponseTo> response = issueMapper.toResponseList(issueRepository.findByTagsId(tagId));
        cache.put(key, response);
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public List<IssueResponseTo> findByTagName(String tagName) {
        ValidationUtils.requireText("tagName", tagName, 2, 32);
        String key = CACHE_PREFIX + "by-tag-name:" + tagName.toLowerCase();
        List<IssueResponseTo> cached = cache.get(key, new TypeReference<List<IssueResponseTo>>() {
        });
        if (cached != null) {
            return cached;
        }
        List<IssueResponseTo> response = issueMapper.toResponseList(
                issueRepository.findAll(toSpecification(new IssueFilter(null, null, tagName))));
        cache.put(key, response);
        return response;
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

    private void evictLists() {
        cache.evictByPrefix(CACHE_PREFIX);
        cache.evictByPrefix(TAG_CACHE_PREFIX);
    }

    private String listKey(IssueFilter filter, Pageable pageable) {
        Long creatorId = filter == null ? null : filter.creatorId();
        String title = filter == null ? null : filter.titleContains();
        String tag = filter == null ? null : filter.tagName();
        String paging = pageable.isPaged()
                ? pageable.getPageNumber() + ":" + pageable.getPageSize()
                : "all";
        String sort = pageable.getSort().isSorted() ? pageable.getSort().toString() : "id: ASC";
        return CACHE_PREFIX + "list:" + creatorId + ":" + title + ":" + tag + ":" + paging + ":" + sort;
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
