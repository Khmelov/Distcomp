package com.example.task330.service.impl;

import com.example.task330.domain.dto.request.TweetRequestTo;
import com.example.task330.domain.dto.response.TweetResponseTo;
import com.example.task330.domain.entity.Marker;
import com.example.task330.domain.entity.Tweet;
import com.example.task330.exception.EntityNotFoundException;
import com.example.task330.mapper.TweetMapper;
import com.example.task330.repository.MarkerRepository;
import com.example.task330.repository.TweetRepository;
import com.example.task330.service.TweetService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TweetServiceImpl implements TweetService {

    private final TweetRepository tweetRepository;
    private final TweetMapper tweetMapper;
    private final MarkerRepository markerRepository;

    @Override
    public TweetResponseTo create(TweetRequestTo request) {
        Tweet tweet = tweetMapper.toEntity(request);
        tweet.setCreated(LocalDateTime.now());
        tweet.setModified(LocalDateTime.now());

        // Логика работы с маркерами остается, так как они в базе Postgres модуля publisher
        if (request.getMarkers() != null) {
            List<Marker> markers = request.getMarkers().stream()
                .map(name -> markerRepository.findByName(name)
                    .orElseGet(() -> markerRepository.save(Marker.builder().name(name).build())))
                .collect(Collectors.toList());
            tweet.setMarkers(markers);
        }

        return tweetMapper.toResponse(tweetRepository.save(tweet));
    }

    @Override
    public List<TweetResponseTo> findAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        return tweetRepository.findAll(pageable).getContent().stream()
                .map(tweetMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public TweetResponseTo findById(Long id) {
        Tweet tweet = tweetRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tweet not found"));
        return tweetMapper.toResponse(tweet);
    }

    @Override
    public TweetResponseTo update(TweetRequestTo request) {
        Tweet existing = tweetRepository.findById(request.getId())
                .orElseThrow(() -> new EntityNotFoundException("Tweet not found"));
        
        Tweet tweet = tweetMapper.toEntity(request);
        tweet.setCreated(existing.getCreated()); 
        tweet.setModified(LocalDateTime.now());   
        
        return tweetMapper.toResponse(tweetRepository.save(tweet));
    }

    @Override
    public void deleteById(Long id) {
        if (!tweetRepository.existsById(id)) {
            throw new EntityNotFoundException("Tweet not found with id: " + id);
        }
        tweetRepository.deleteById(id);
    }
}