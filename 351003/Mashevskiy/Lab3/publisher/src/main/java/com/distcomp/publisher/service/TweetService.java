package com.distcomp.publisher.service;

import com.distcomp.publisher.dto.TweetRequestDTO;
import com.distcomp.publisher.dto.TweetResponseDTO;
import com.distcomp.publisher.model.Tweet;
import com.distcomp.publisher.model.TweetMarker;
import com.distcomp.publisher.repository.TweetRepository;
import com.distcomp.publisher.repository.TweetMarkerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TweetService {

    @Autowired
    private TweetRepository tweetRepository;

    @Autowired
    private TweetMarkerRepository tweetMarkerRepository;

    public List<TweetResponseDTO> getAll() {
        return tweetRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public TweetResponseDTO getById(Long id) {
        Tweet tweet = tweetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tweet not found"));
        return toResponse(tweet);
    }

    @Transactional
    public TweetResponseDTO create(TweetRequestDTO dto) {
        Tweet tweet = new Tweet();
        tweet.setCreatorId(dto.getCreatorId());
        tweet.setTitle(dto.getTitle());
        tweet.setContent(dto.getContent());
        Tweet saved = tweetRepository.save(tweet);

        if (dto.getMarkerIds() != null) {
            for (Long markerId : dto.getMarkerIds()) {
                TweetMarker tm = new TweetMarker();
                tm.setTweetId(saved.getId());
                tm.setMarkerId(markerId);
                tweetMarkerRepository.save(tm);
            }
        }
        return toResponse(saved);
    }

    @Transactional
    public TweetResponseDTO update(Long id, TweetRequestDTO dto) {
        Tweet tweet = tweetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tweet not found"));
        tweet.setCreatorId(dto.getCreatorId());
        tweet.setTitle(dto.getTitle());
        tweet.setContent(dto.getContent());

        tweetMarkerRepository.deleteByTweetId(id);
        if (dto.getMarkerIds() != null) {
            for (Long markerId : dto.getMarkerIds()) {
                TweetMarker tm = new TweetMarker();
                tm.setTweetId(id);
                tm.setMarkerId(markerId);
                tweetMarkerRepository.save(tm);
            }
        }
        return toResponse(tweetRepository.save(tweet));
    }

    @Transactional
    public void delete(Long id) {
        tweetMarkerRepository.deleteByTweetId(id);
        tweetRepository.deleteById(id);
    }

    private TweetResponseDTO toResponse(Tweet tweet) {
        TweetResponseDTO dto = new TweetResponseDTO();
        dto.setId(tweet.getId());
        dto.setCreatorId(tweet.getCreatorId());
        dto.setTitle(tweet.getTitle());
        dto.setContent(tweet.getContent());
        dto.setCreated(tweet.getCreated());
        dto.setModified(tweet.getModified());

        List<Long> markerIds = tweetMarkerRepository.findByTweetId(tweet.getId())
                .stream().map(TweetMarker::getMarkerId).collect(Collectors.toList());
        dto.setMarkerIds(markerIds);
        return dto;
    }
}