package com.tweetmarkersservice.services;

import com.tweetmarkersservice.configs.markerclientconfig.MarkerClient;
import com.tweetmarkersservice.dtos.MarkerResponseByNameTo;
import com.tweetmarkersservice.dtos.TweetMarkerRequestTo;
import com.tweetmarkersservice.dtos.TweetMarkersRequestByNameTo;
import com.tweetmarkersservice.dtos.TweetMarkersResponseTo;
import com.tweetmarkersservice.models.TweetMarkers;
import com.tweetmarkersservice.repositories.TweetMarkersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class TweetMarkersService {

    private final TweetMarkersRepository tweetMarkersRepository;
    private final MarkerClient markerClient;

    public TweetMarkersResponseTo linkTweetMarker(TweetMarkerRequestTo request) {
        if (tweetMarkersRepository.existsByTweetIdAndMarkerId(request.getTweetId(), request.getMarkerId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Link exists");
        }
        TweetMarkers saved = tweetMarkersRepository.save(toEntity(request));
        return toDto(saved);
    }

    @Transactional
    public void linkTweetMarkersByNames(List<TweetMarkersRequestByNameTo> requestList) {
        if (requestList == null || requestList.isEmpty()) {
            return;
        }

        List<Long> markerIds = markerClient.getMarkersByNames(requestList).stream()
                .map(MarkerResponseByNameTo::getId)
                .distinct()
                .toList();

        Long tweetId = requestList.getFirst().getTweetId();

        for (Long markerId : markerIds) {
            if (tweetMarkersRepository.existsByTweetIdAndMarkerId(tweetId, markerId)) {
                continue;
            }
            tweetMarkersRepository.save(toEntity(new TweetMarkerRequestTo(tweetId, markerId)));
        }
    }

    @Transactional
    public List<Long> unlinkByTweetId(Long tweetId) {
        List<Long> markerIds = tweetMarkersRepository.findMarkerIdsByTweetId(tweetId).stream()
                .distinct()
                .toList();

        if (markerIds.isEmpty()) {
            return List.of();
        }

        tweetMarkersRepository.deleteAllByTweetId(tweetId);

        Set<Long> stillUsed = new HashSet<>(tweetMarkersRepository.findUsedMarkerIds(markerIds));

        return markerIds.stream()
                .filter(markerId -> !stillUsed.contains(markerId))
                .toList();
    }

    public List<Long> getMarkersByTweetId(Long tweetId) {
        return tweetMarkersRepository.findMarkerIdsByTweetId(tweetId);
    }

    private TweetMarkers toEntity(TweetMarkerRequestTo request) {
        return TweetMarkers.builder()
                .tweetId(request.getTweetId())
                .markerId(request.getMarkerId())
                .build();
    }

    private TweetMarkersResponseTo toDto(TweetMarkers entity) {
        return TweetMarkersResponseTo.builder()
                .tweetId(entity.getTweetId())
                .markerId(entity.getMarkerId())
                .build();
    }
}
