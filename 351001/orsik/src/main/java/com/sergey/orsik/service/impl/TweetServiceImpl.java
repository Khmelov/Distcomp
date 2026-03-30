package com.sergey.orsik.service.impl;

import com.sergey.orsik.dto.request.TweetRequestTo;
import com.sergey.orsik.dto.response.TweetResponseTo;
import com.sergey.orsik.entity.Tweet;
import com.sergey.orsik.exception.EntityNotFoundException;
import com.sergey.orsik.mapper.TweetMapper;
import com.sergey.orsik.repository.CrudRepository;
import com.sergey.orsik.service.TweetService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TweetServiceImpl implements TweetService {

    private final CrudRepository<Tweet> repository;
    private final TweetMapper mapper;

    public TweetServiceImpl(CrudRepository<Tweet> repository, TweetMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public List<TweetResponseTo> findAll() {
        return repository.findAll().stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public TweetResponseTo findById(Long id) {
        Tweet entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tweet", id));
        return mapper.toResponse(entity);
    }

    @Override
    public TweetResponseTo create(TweetRequestTo request) {
        Tweet entity = mapper.toEntity(request);
        entity.setId(null);
        Tweet saved = repository.save(entity);
        return mapper.toResponse(saved);
    }

    @Override
    public TweetResponseTo update(Long id, TweetRequestTo request) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Tweet", id);
        }
        Tweet entity = mapper.toEntity(request);
        entity.setId(id);
        Tweet saved = repository.save(entity);
        return mapper.toResponse(saved);
    }

    @Override
    public void deleteById(Long id) {
        if (!repository.deleteById(id)) {
            throw new EntityNotFoundException("Tweet", id);
        }
    }
}
