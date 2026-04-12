package com.sergey.orsik.service.impl;

import com.sergey.orsik.dto.request.CommentRequestTo;
import com.sergey.orsik.dto.response.CommentResponseTo;
import com.sergey.orsik.entity.Comment;
import com.sergey.orsik.exception.EntityNotFoundException;
import com.sergey.orsik.mapper.CommentMapper;
import com.sergey.orsik.repository.CommentRepository;
import com.sergey.orsik.repository.TweetRepository;
import com.sergey.orsik.service.CommentService;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@Transactional
public class CommentServiceImpl implements CommentService {

    private final CommentRepository repository;
    private final TweetRepository tweetRepository;
    private final CommentMapper mapper;

    public CommentServiceImpl(CommentRepository repository, TweetRepository tweetRepository, CommentMapper mapper) {
        this.repository = repository;
        this.tweetRepository = tweetRepository;
        this.mapper = mapper;
    }

    @Override
    public List<CommentResponseTo> findAll(int page, int size, String sortBy, String sortDir, Long tweetId, String content) {
        Pageable pageable = PageRequest.of(page, size, buildSort(sortBy, sortDir));
        Specification<Comment> spec = (root, query, cb) -> cb.conjunction();
        if (tweetId != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("tweetId"), tweetId));
        }
        if (StringUtils.hasText(content)) {
            String pattern = "%" + content.toLowerCase() + "%";
            spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.get("content")), pattern));
        }
        return repository.findAll(spec, pageable).stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public CommentResponseTo findById(Long id) {
        Comment entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Comment", id));
        return mapper.toResponse(entity);
    }

    @Override
    public CommentResponseTo create(CommentRequestTo request) {
        validateTweetExists(request.getTweetId());
        Comment entity = mapper.toEntity(request);
        entity.setId(null);
        Comment saved = repository.save(entity);
        return mapper.toResponse(saved);
    }

    @Override
    public CommentResponseTo update(Long id, CommentRequestTo request) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Comment", id);
        }
        validateTweetExists(request.getTweetId());
        Comment entity = mapper.toEntity(request);
        entity.setId(id);
        Comment saved = repository.save(entity);
        return mapper.toResponse(saved);
    }

    @Override
    public void deleteById(Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Comment", id);
        }
        repository.deleteById(id);
    }

    private void validateTweetExists(Long tweetId) {
        if (!tweetRepository.existsById(tweetId)) {
            throw new EntityNotFoundException("Tweet", tweetId);
        }
    }

    private Sort buildSort(String sortBy, String sortDir) {
        String targetField = StringUtils.hasText(sortBy) ? sortBy : "id";
        Sort.Direction direction = "desc".equalsIgnoreCase(sortDir) ? Sort.Direction.DESC : Sort.Direction.ASC;
        return Sort.by(direction, targetField);
    }
}
