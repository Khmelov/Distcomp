package by.tracker.rest_api.service;

import by.tracker.rest_api.dto.TweetRequestDto;
import by.tracker.rest_api.dto.TweetResponseDto;
import by.tracker.rest_api.entity.Creator;
import by.tracker.rest_api.entity.Marker;
import by.tracker.rest_api.entity.Tweet;
import by.tracker.rest_api.exception.DuplicateResourceException;
import by.tracker.rest_api.exception.ResourceNotFoundException;
import by.tracker.rest_api.exception.ValidationException;
import by.tracker.rest_api.repository.CreatorRepository;
import by.tracker.rest_api.repository.MarkerRepository;
import by.tracker.rest_api.repository.TweetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class TweetService {

    @Autowired
    private TweetRepository tweetRepository;

    @Autowired
    private CreatorRepository creatorRepository;

    @Autowired
    private MarkerRepository markerRepository;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;

    @Transactional(readOnly = true)
    public TweetResponseDto getById(Long id) {
        Tweet tweet = tweetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tweet not found", 40402));
        return toResponseDto(tweet);
    }

    @Transactional(readOnly = true)
    public Page<TweetResponseDto> getAll(Pageable pageable) {
        return tweetRepository.findAll(pageable).map(this::toResponseDto);
    }

    public TweetResponseDto create(TweetRequestDto dto) {
        if (dto.getTitle() == null || dto.getTitle().length() < 2 || dto.getTitle().length() > 64) {
            throw new ValidationException("Title must be between 2 and 64 characters", 40010);
        }
        if (dto.getContent() == null || dto.getContent().length() < 4 || dto.getContent().length() > 2048) {
            throw new ValidationException("Content must be between 4 and 2048 characters", 40011);
        }

        if (tweetRepository.existsByTitle(dto.getTitle())) {
            throw new DuplicateResourceException("Tweet with this title already exists", 40302);
        }

        Creator creator = creatorRepository.findById(dto.getCreatorId())
                .orElseThrow(() -> new ResourceNotFoundException("Creator not found", 40401));

        Tweet tweet = new Tweet();
        tweet.setCreator(creator);
        tweet.setTitle(dto.getTitle());
        tweet.setContent(dto.getContent());

        if (dto.getMarkerIds() != null && !dto.getMarkerIds().isEmpty()) {
            List<Marker> markers = markerRepository.findAllById(dto.getMarkerIds());
            tweet.setMarkers(markers);
        }

        tweet = tweetRepository.save(tweet);
        return toResponseDto(tweet);
    }

    public TweetResponseDto update(TweetRequestDto dto) {
        if (dto.getId() == null) {
            throw new ValidationException("ID is required for update", 40012);
        }

        Tweet existingTweet = tweetRepository.findById(dto.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Tweet not found", 40402));

        if (dto.getTitle() != null && !dto.getTitle().equals(existingTweet.getTitle())) {
            if (tweetRepository.existsByTitle(dto.getTitle())) {
                throw new DuplicateResourceException("Tweet with this title already exists", 40302);
            }
            existingTweet.setTitle(dto.getTitle());
        }

        if (dto.getContent() != null) {
            existingTweet.setContent(dto.getContent());
        }

        if (dto.getCreatorId() != null && !existingTweet.getCreator().getId().equals(dto.getCreatorId())) {
            Creator creator = creatorRepository.findById(dto.getCreatorId())
                    .orElseThrow(() -> new ResourceNotFoundException("Creator not found", 40401));
            existingTweet.setCreator(creator);
        }

        if (dto.getMarkerIds() != null) {
            List<Marker> markers = markerRepository.findAllById(dto.getMarkerIds());
            existingTweet.setMarkers(markers);
        }

        existingTweet = tweetRepository.save(existingTweet);
        return toResponseDto(existingTweet);
    }

    public void delete(Long id) {
        if (!tweetRepository.existsById(id)) {
            throw new ResourceNotFoundException("Tweet not found", 40402);
        }
        tweetRepository.deleteById(id);
    }

    private TweetResponseDto toResponseDto(Tweet entity) {
        TweetResponseDto dto = new TweetResponseDto();
        dto.setId(entity.getId());
        dto.setCreatorId(entity.getCreator() != null ? entity.getCreator().getId() : null);
        dto.setTitle(entity.getTitle());
        dto.setContent(entity.getContent());
        if (entity.getCreated() != null) {
            dto.setCreated(entity.getCreated().format(formatter));
        }
        if (entity.getModified() != null) {
            dto.setModified(entity.getModified().format(formatter));
        }
        if (entity.getMarkers() != null) {
            dto.setMarkerIds(entity.getMarkers().stream().map(Marker::getId).collect(Collectors.toList()));
        }
        return dto;
    }
}