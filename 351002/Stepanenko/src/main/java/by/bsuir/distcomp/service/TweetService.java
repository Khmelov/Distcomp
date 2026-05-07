package by.bsuir.distcomp.service;

import by.bsuir.distcomp.dto.request.TweetRequestTo;
import by.bsuir.distcomp.dto.response.TweetResponseTo;
import by.bsuir.distcomp.entity.Tweet;
import by.bsuir.distcomp.exception.ResourceNotFoundException;
import by.bsuir.distcomp.mapper.TweetMapper;
import by.bsuir.distcomp.repository.impl.InMemoryAuthorRepository;
import by.bsuir.distcomp.repository.impl.InMemoryMarkerRepository;
import by.bsuir.distcomp.repository.impl.InMemoryTweetRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TweetService {
    private final InMemoryTweetRepository tweetRepository;
    private final InMemoryAuthorRepository authorRepository;
    private final InMemoryMarkerRepository markerRepository;
    private final TweetMapper tweetMapper;

    public TweetService(InMemoryTweetRepository tweetRepository,
                        InMemoryAuthorRepository authorRepository,
                        InMemoryMarkerRepository markerRepository,
                        TweetMapper tweetMapper) {
        this.tweetRepository = tweetRepository;
        this.authorRepository = authorRepository;
        this.markerRepository = markerRepository;
        this.tweetMapper = tweetMapper;
    }

    public TweetResponseTo create(TweetRequestTo dto) {
        if (!authorRepository.existsById(dto.getAuthorId())) {
            throw new ResourceNotFoundException("Author with id " + dto.getAuthorId() + " not found", 40404);
        }
        
        if (dto.getMarkerIds() != null && !dto.getMarkerIds().isEmpty()) {
            for (Long markerId : dto.getMarkerIds()) {
                if (!markerRepository.existsById(markerId)) {
                    throw new ResourceNotFoundException("Marker with id " + markerId + " not found", 40405);
                }
            }
        }
        
        Tweet entity = tweetMapper.toEntity(dto);
        entity.setCreated(LocalDateTime.now());
        entity.setModified(LocalDateTime.now());
        if (dto.getMarkerIds() != null) {
            entity.setMarkerIds(dto.getMarkerIds());
        }
        Tweet saved = tweetRepository.save(entity);
        return tweetMapper.toResponseDto(saved);
    }

    public TweetResponseTo getById(Long id) {
        Tweet entity = tweetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tweet with id " + id + " not found", 40406));
        return tweetMapper.toResponseDto(entity);
    }

    public List<TweetResponseTo> getAll() {
        return tweetRepository.findAll().stream()
                .map(tweetMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    public TweetResponseTo update(TweetRequestTo dto) {
        Tweet existingTweet = tweetRepository.findById(dto.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Tweet with id " + dto.getId() + " not found", 40407));
        
        if (!authorRepository.existsById(dto.getAuthorId())) {
            throw new ResourceNotFoundException("Author with id " + dto.getAuthorId() + " not found", 40408);
        }
        
        if (dto.getMarkerIds() != null && !dto.getMarkerIds().isEmpty()) {
            for (Long markerId : dto.getMarkerIds()) {
                if (!markerRepository.existsById(markerId)) {
                    throw new ResourceNotFoundException("Marker with id " + markerId + " not found", 40409);
                }
            }
        }
        
        Tweet entity = tweetMapper.toEntity(dto);
        entity.setCreated(existingTweet.getCreated());
        entity.setModified(LocalDateTime.now());
        if (dto.getMarkerIds() != null) {
            entity.setMarkerIds(dto.getMarkerIds());
        } else {
            entity.setMarkerIds(existingTweet.getMarkerIds());
        }
        Tweet updated = tweetRepository.update(entity);
        return tweetMapper.toResponseDto(updated);
    }

    public void deleteById(Long id) {
        if (!tweetRepository.existsById(id)) {
            throw new ResourceNotFoundException("Tweet with id " + id + " not found", 40410);
        }
        tweetRepository.deleteById(id);
    }

    public TweetResponseTo getAuthorByTweetId(Long tweetId) {
        Tweet tweet = tweetRepository.findById(tweetId)
                .orElseThrow(() -> new ResourceNotFoundException("Tweet with id " + tweetId + " not found", 40411));
        return getById(tweetId);
    }

    public List<TweetResponseTo> getTweetsByMarkerNames(Set<String> markerNames,
                                                         Set<Long> markerIds,
                                                         String authorLogin,
                                                         String title,
                                                         String content) {
        List<Tweet> tweets = tweetRepository.findAll();
        
        Set<Long> finalMarkerIds = markerIds;
        if (markerNames != null && !markerNames.isEmpty()) {
            Set<Long> markerIdsByName = markerRepository.findAll().stream()
                    .filter(m -> markerNames.contains(m.getName()))
                    .map(m -> m.getId())
                    .collect(Collectors.toSet());
            if (finalMarkerIds == null) {
                finalMarkerIds = markerIdsByName;
            } else {
                finalMarkerIds = new HashSet<>(finalMarkerIds);
                finalMarkerIds.addAll(markerIdsByName);
            }
        }
        
        final Set<Long> markerIdsForFilter = finalMarkerIds;
        if (markerIdsForFilter != null && !markerIdsForFilter.isEmpty()) {
            tweets = tweets.stream()
                    .filter(t -> t.getMarkerIds() != null && 
                            t.getMarkerIds().stream().anyMatch(markerIdsForFilter::contains))
                    .collect(Collectors.toList());
        }
        
        if (authorLogin != null && !authorLogin.isEmpty()) {
            Set<Long> authorIds = authorRepository.findAll().stream()
                    .filter(a -> authorLogin.equals(a.getLogin()))
                    .map(a -> a.getId())
                    .collect(Collectors.toSet());
            tweets = tweets.stream()
                    .filter(t -> authorIds.contains(t.getAuthorId()))
                    .collect(Collectors.toList());
        }
        
        if (title != null && !title.isEmpty()) {
            tweets = tweets.stream()
                    .filter(t -> title.equals(t.getTitle()))
                    .collect(Collectors.toList());
        }
        
        if (content != null && !content.isEmpty()) {
            tweets = tweets.stream()
                    .filter(t -> t.getContent() != null && t.getContent().contains(content))
                    .collect(Collectors.toList());
        }
        
        return tweets.stream()
                .map(tweetMapper::toResponseDto)
                .collect(Collectors.toList());
    }
}
