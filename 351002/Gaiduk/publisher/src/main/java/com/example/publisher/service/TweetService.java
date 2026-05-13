package com.example.publisher.service;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import com.example.publisher.dto.TweetRequestTo;
import com.example.publisher.dto.TweetResponseTo;
import com.example.publisher.entity.Tweet;
import com.example.publisher.entity.User;
import com.example.publisher.entity.Sticker;
import com.example.publisher.repository.TweetRepository;
import com.example.publisher.repository.UserRepository;
import com.example.publisher.repository.StickerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TweetService {

    private final TweetRepository tweetRepository;
    private final UserRepository userRepository;
    private final StickerRepository stickerRepository;

    @Transactional
    public TweetResponseTo create(TweetRequestTo dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "User not found"));

        boolean exists = tweetRepository.existsByUserIdAndTitle(dto.getUserId(), dto.getTitle());
        if (exists) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Tweet with same title already exists for this user");
        }

        Tweet tweet = new Tweet();
        tweet.setUser(user);
        tweet.setTitle(dto.getTitle());
        tweet.setContent(dto.getContent());
        tweet.setCreated(LocalDateTime.now());
        tweet.setModified(LocalDateTime.now());

        if (dto.getStickerIds() != null && !dto.getStickerIds().isEmpty()) {
            List<Sticker> stickers = stickerRepository.findAllById(dto.getStickerIds());
            if (stickers.size() != dto.getStickerIds().size()) {
                throw new IllegalArgumentException("One or more stickers not found");
            }
            tweet.setStickers(stickers);
        }

        return toResponse(tweetRepository.save(tweet));
    }

    @Transactional(readOnly = true)
    public Page<TweetResponseTo> getAll(Pageable pageable) {
        return tweetRepository.findAll(pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public TweetResponseTo get(Long id) {
        Tweet tweet = tweetRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Tweet not found"));
        return toResponse(tweet);
    }

    @Transactional
    public TweetResponseTo update(Long id, TweetRequestTo dto) {
        Tweet tweet = tweetRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Tweet not found"));

        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        tweet.setUser(user);
        tweet.setTitle(dto.getTitle());
        tweet.setContent(dto.getContent());
        tweet.setModified(LocalDateTime.now());

        if (dto.getStickerIds() != null) {
            List<Sticker> stickers = stickerRepository.findAllById(dto.getStickerIds());
            if (stickers.size() != dto.getStickerIds().size()) {
                throw new IllegalArgumentException("One or more stickers not found");
            }
            tweet.setStickers(stickers);
        } else {
            tweet.setStickers(Collections.emptyList());
        }

        return toResponse(tweetRepository.save(tweet));
    }

    @Transactional
    public void delete(Long id) {
        if (!tweetRepository.existsById(id)) {
            throw new NoSuchElementException("Tweet not found with id: " + id);
        }
        tweetRepository.deleteById(id);
    }

    private TweetResponseTo toResponse(Tweet tweet) {
        Set<Long> stickerIds = tweet.getStickers().stream()
                .map(Sticker::getId)
                .collect(Collectors.toSet());
        return new TweetResponseTo(
                tweet.getId(),
                tweet.getUser().getId(),
                tweet.getTitle(),
                tweet.getContent(),
                tweet.getCreated(),
                tweet.getModified(),
                stickerIds
        );
    }
}