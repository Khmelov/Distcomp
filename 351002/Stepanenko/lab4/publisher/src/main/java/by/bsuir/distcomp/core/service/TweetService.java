package by.bsuir.distcomp.core.service;

import by.bsuir.distcomp.dto.request.TweetRequestTo;
import by.bsuir.distcomp.dto.response.TweetResponseTo;
import by.bsuir.distcomp.core.domain.*;
import by.bsuir.distcomp.core.exception.DuplicateException;
import by.bsuir.distcomp.core.exception.ResourceNotFoundException;
import by.bsuir.distcomp.client.DiscussionReactionClient;
import by.bsuir.distcomp.core.mapper.TweetMapper;
import by.bsuir.distcomp.core.repository.*;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class TweetService {

    private static final Logger log = LoggerFactory.getLogger(TweetService.class);

    private final TweetRepository tweetRepository;
    private final AuthorRepository authorRepository;
    private final MarkerRepository markerRepository;
    private final TweetMapper tweetMapper;
    private final DiscussionReactionClient discussionReactionClient;

    public TweetService(TweetRepository tweetRepository, AuthorRepository authorRepository,
                        MarkerRepository markerRepository, TweetMapper tweetMapper,
                        DiscussionReactionClient discussionReactionClient) {
        this.tweetRepository = tweetRepository;
        this.authorRepository = authorRepository;
        this.markerRepository = markerRepository;
        this.tweetMapper = tweetMapper;
        this.discussionReactionClient = discussionReactionClient;
    }

    public TweetResponseTo create(TweetRequestTo request) {
        if (tweetRepository.existsByTitle(request.getTitle())) {
            throw new DuplicateException("Tweet title already exists", 40305);
        }

        Author author = authorRepository.findById(request.getAuthorId())
                .orElseThrow(() -> new ResourceNotFoundException("Author not found", 40404));

        Tweet tweet = tweetMapper.toEntity(request);
        tweet.setId(null);
        tweet.setAuthor(author);
        tweet.setCreated(LocalDateTime.now());
        tweet.setModified(LocalDateTime.now());

        // Обработка маркеров (и по ID, и по именам из JSON)
        tweet.setMarkers(processMarkersFromRequest(request));

        return tweetMapper.toResponseDto(tweetRepository.save(tweet));
    }

    public TweetResponseTo update(TweetRequestTo request) {
        Tweet existing = tweetRepository.findById(request.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Tweet not found", 40407));

        if (!existing.getTitle().equals(request.getTitle()) && tweetRepository.existsByTitle(request.getTitle())) {
            throw new DuplicateException("Tweet title already exists", 40306);
        }

        existing.setTitle(request.getTitle());
        existing.setContent(request.getContent());
        existing.setModified(LocalDateTime.now());

        // Обновляем маркеры
        existing.setMarkers(processMarkersFromRequest(request));

        return tweetMapper.toResponseDto(tweetRepository.save(existing));
    }

    public TweetResponseTo getById(Long id) {
        return tweetRepository.findById(id)
                .map(tweetMapper::toResponseDto)
                .orElseThrow(() -> new ResourceNotFoundException("Tweet not found", 40406));
    }

    public void deleteById(Long id) {
        Tweet tweet = tweetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tweet not found", 40410));

        Set<Marker> associatedMarkers = new HashSet<>(tweet.getMarkers());

        // Каскадное удаление реакций через Kafka
        try {
            discussionReactionClient.deleteByTweetId(id);
        } catch (Exception ex) {
            // Логируем, но НЕ прерываем удаление самого твита, если обсуждения недоступны
            log.error("Could not delete reactions for tweet {}: {}", id, ex.getMessage());
        }

        tweetRepository.delete(tweet);
        tweetRepository.flush();

        for (Marker marker : associatedMarkers) {
            if (tweetRepository.countByMarkersId(marker.getId()) == 0) {
                markerRepository.delete(marker);
            }
        }
    }

    public List<TweetResponseTo> search(List<String> markerNames, List<Long> markerIds, String authorLogin, String title, String content, Pageable pageable) {
        Specification<Tweet> spec = (root, query, cb) -> {
            query.distinct(true);
            List<Predicate> predicates = new ArrayList<>();

            if ((markerNames != null && !markerNames.isEmpty()) || (markerIds != null && !markerIds.isEmpty())) {
                Join<Tweet, Marker> markerJoin = root.join("markers");
                if (markerNames != null && !markerNames.isEmpty()) {
                    predicates.add(markerJoin.get("name").in(markerNames));
                }
                if (markerIds != null && !markerIds.isEmpty()) {
                    predicates.add(markerJoin.get("id").in(markerIds));
                }
            }

            if (authorLogin != null && !authorLogin.isEmpty()) {
                predicates.add(cb.equal(root.join("author").get("login"), authorLogin));
            }
            if (title != null && !title.isEmpty()) {
                predicates.add(cb.like(root.get("title"), "%" + title + "%"));
            }
            if (content != null && !content.isEmpty()) {
                predicates.add(cb.like(root.get("content"), "%" + content + "%"));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return tweetRepository.findAll(spec, pageable).stream()
                .map(tweetMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    private Set<Marker> processMarkersFromRequest(TweetRequestTo request) {
        Set<Marker> markers = new HashSet<>();

        // 1. Поиск/Создание маркеров по текстовым именам (поле "markers" в JSON)
        if (request.getMarkers() != null) {
            for (String markerName : request.getMarkers()) {
                Marker marker = markerRepository.findByName(markerName)
                        .orElseGet(() -> {
                            Marker newMarker = new Marker();
                            newMarker.setName(markerName);
                            return markerRepository.save(newMarker);
                        });
                markers.add(marker);
            }
        }

        // 2. Поиск маркеров по ID (поле "markerIds" в JSON)
        if (request.getMarkerIds() != null && !request.getMarkerIds().isEmpty()) {
            markers.addAll(markerRepository.findAllById(request.getMarkerIds()));
        }

        return markers;
    }
}