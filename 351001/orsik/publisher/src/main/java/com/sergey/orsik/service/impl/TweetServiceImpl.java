package com.sergey.orsik.service.impl;

import com.sergey.orsik.dto.request.TweetRequestTo;
import com.sergey.orsik.dto.response.TweetResponseTo;
import com.sergey.orsik.entity.Label;
import com.sergey.orsik.entity.Tweet;
import com.sergey.orsik.exception.EntityNotFoundException;
import com.sergey.orsik.mapper.TweetMapper;
import com.sergey.orsik.repository.CreatorRepository;
import com.sergey.orsik.repository.LabelRepository;
import com.sergey.orsik.repository.TweetRepository;
import com.sergey.orsik.client.DiscussionCommentsClient;
import com.sergey.orsik.service.TweetService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
public class TweetServiceImpl implements TweetService {

    private static final Logger log = LoggerFactory.getLogger(TweetServiceImpl.class);

    private final TweetRepository repository;
    private final CreatorRepository creatorRepository;
    private final LabelRepository labelRepository;
    private final TweetMapper mapper;
    private final DiscussionCommentsClient discussionCommentsClient;
    private final TweetDeletionHelper tweetDeletionHelper;

    public TweetServiceImpl(
            TweetRepository repository,
            CreatorRepository creatorRepository,
            LabelRepository labelRepository,
            TweetMapper mapper,
            DiscussionCommentsClient discussionCommentsClient,
            TweetDeletionHelper tweetDeletionHelper) {
        this.repository = repository;
        this.creatorRepository = creatorRepository;
        this.labelRepository = labelRepository;
        this.mapper = mapper;
        this.discussionCommentsClient = discussionCommentsClient;
        this.tweetDeletionHelper = tweetDeletionHelper;
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(
            value = "tweets:list",
            key = "T(java.util.Objects).hash(#page, #size, #sortBy, #sortDir, #creatorId, #title)")
    public List<TweetResponseTo> findAll(int page, int size, String sortBy, String sortDir, Long creatorId, String title) {
        Pageable pageable = PageRequest.of(page, size, buildSort(sortBy, sortDir));
        Specification<Tweet> spec = (root, query, cb) -> cb.conjunction();
        if (creatorId != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("creatorId"), creatorId));
        }
        if (StringUtils.hasText(title)) {
            String pattern = "%" + title.toLowerCase() + "%";
            spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.get("title")), pattern));
        }
        return repository.findAll(spec, pageable).stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "tweets", key = "#id")
    public TweetResponseTo findById(Long id) {
        Tweet entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tweet", id));
        return mapper.toResponse(entity);
    }

    @Override
    @Transactional
    @CacheEvict(value = "tweets:list", allEntries = true)
    public TweetResponseTo create(TweetRequestTo request) {
        log.info("Processing tweet create in service: creatorId={}, title='{}', content='{}'",
                request.getCreatorId(), request.getTitle(), request.getContent());
        validateCreatorExists(request.getCreatorId());
        if (repository.existsByTitle(request.getTitle())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Tweet with title '%s' already exists".formatted(request.getTitle())
            );
        }
        Tweet entity = mapper.toEntity(request);
        entity.setId(null);
        entity.setLabels(resolveLabels(request.getLabels()));
        Tweet saved = repository.save(entity);
        return mapper.toResponse(saved);
    }

    @Override
    @Transactional
    @Caching(
            evict = {
                @CacheEvict(value = "tweets", key = "#id"),
                @CacheEvict(value = "tweets:list", allEntries = true)
            })
    public TweetResponseTo update(Long id, TweetRequestTo request) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Tweet", id);
        }
        validateCreatorExists(request.getCreatorId());
        if (repository.existsByTitleAndIdNot(request.getTitle(), id)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Tweet with title '%s' already exists".formatted(request.getTitle())
            );
        }
        Tweet entity = mapper.toEntity(request);
        entity.setId(id);
        entity.setLabels(resolveLabels(request.getLabels()));
        Tweet saved = repository.save(entity);
        deleteOrphanLabels();
        return mapper.toResponse(saved);
    }

    @Override
    @Caching(
            evict = {
                @CacheEvict(value = "tweets", key = "#id"),
                @CacheEvict(value = "tweets:list", allEntries = true),
                @CacheEvict(value = "comments:list", allEntries = true)
            })
    public void deleteById(Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Tweet", id);
        }
        discussionCommentsClient.deleteAllForTweet(id);
        tweetDeletionHelper.deleteTweetAndOrphanLabels(id);
    }

    private void validateCreatorExists(Long creatorId) {
        if (!creatorRepository.existsById(creatorId)) {
            throw new EntityNotFoundException("Creator", creatorId);
        }
    }

    private Sort buildSort(String sortBy, String sortDir) {
        String targetField = StringUtils.hasText(sortBy) ? sortBy : "id";
        Sort.Direction direction = "desc".equalsIgnoreCase(sortDir) ? Sort.Direction.DESC : Sort.Direction.ASC;
        return Sort.by(direction, targetField);
    }

    private Set<Label> resolveLabels(Set<String> labelNames) {
        Set<String> safeLabelNames = labelNames == null ? Collections.emptySet() : labelNames;
        Set<Label> labels = new LinkedHashSet<>();
        for (String name : safeLabelNames) {
            Label label = labelRepository.findByName(name)
                    .orElseGet(() -> labelRepository.save(new Label(null, name, new LinkedHashSet<>())));
            labels.add(label);
        }
        return labels;
    }

    private void deleteOrphanLabels() {
        List<Label> orphanLabels = labelRepository.findAllByTweetsIsEmpty();
        if (!orphanLabels.isEmpty()) {
            labelRepository.deleteAll(orphanLabels);
        }
    }
}
