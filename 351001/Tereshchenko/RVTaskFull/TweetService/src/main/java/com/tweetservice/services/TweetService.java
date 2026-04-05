package com.tweetservice.services;

import com.tweetservice.configs.exceptionhandlerconfig.exceptions.TweetAlreadyExistsException;
import com.tweetservice.configs.exceptionhandlerconfig.exceptions.TweetNotFoundException;
import com.tweetservice.configs.exceptionhandlerconfig.exceptions.TweetTitleAlreadyExistsException;
import com.tweetservice.configs.exceptionhandlerconfig.exceptions.WriterNotFoundException;
import com.tweetservice.configs.markerclientconfig.MarkerClient;
import com.tweetservice.configs.messagesclientconfig.MessageClient;
import com.tweetservice.configs.tweetmarkersclientconfig.TweetMarkersClient;
import com.tweetservice.configs.writerclientconfig.WriterClient;
import com.tweetservice.dtos.TweetRequestTo;
import com.tweetservice.dtos.TweetResponseTo;
import com.tweetservice.dtos.marker.MarkerResponseTo;
import com.tweetservice.dtos.message.MessageResponseTo;
import com.tweetservice.dtos.tweetmarkers.TweetMarkersRequestByNameTo;
import com.tweetservice.dtos.writer.WriterResponseTo;
import com.tweetservice.models.Tweet;
import com.tweetservice.repositories.TweetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TweetService {

    private final TweetRepository tweetRepository;
    private final WriterClient writerClient;
    private final MessageClient messageClient;
    private final MarkerClient markerClient;
    private final TweetMarkersClient tweetMarkersClient;

    @Transactional
    public TweetResponseTo createTweet(TweetRequestTo request) {
        if (tweetRepository.findTweetByTitle(request.getTitle()).isPresent()) {
            throw new TweetTitleAlreadyExistsException("Tweet title already exists");
        }

        validateWriterExists(request.getWriterId());

        Tweet saved = tweetRepository.save(toEntity(request));

        List<TweetMarkersRequestByNameTo> markerRequests =
                (request.getMarkerNames() == null ? List.<String>of() : request.getMarkerNames())
                        .stream()
                        .map(name -> name == null ? "" : name.trim())
                        .filter(name -> !name.isEmpty())
                        .distinct()
                        .map(name -> new TweetMarkersRequestByNameTo(saved.getId(), name))
                        .toList();

        if (!markerRequests.isEmpty()) {
            tweetMarkersClient.linkTweetMarkers(markerRequests);
        }

        return toDto(saved);
    }

    public List<TweetResponseTo> findAllTweets() {
        return tweetRepository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    public TweetResponseTo findTweetById(Long id) {
        Tweet tweet = tweetRepository.findTweetById(id)
                .orElseThrow(() -> new TweetNotFoundException("Tweet not found"));
        return toDto(tweet);
    }

    public TweetResponseTo updateTweetById(TweetRequestTo request, Long id) {
        Tweet tweet = tweetRepository.findTweetById(id)
                .orElseThrow(() -> new TweetNotFoundException("Tweet not found"));

        if (tweetRepository.existsByTitleAndIdNot(request.getTitle(), id)) {
            throw new TweetAlreadyExistsException("Tweet already exists");
        }

        tweet.setContent(request.getContent());
        tweet.setTitle(request.getTitle());

        Tweet updated = tweetRepository.save(tweet);
        return toDto(updated);
    }

    @Transactional
    public void deleteTweetById(Long id) {
        if (tweetRepository.findTweetById(id).isEmpty()) {
            throw new TweetNotFoundException("Tweet not found");
        }

        messageClient.deleteMessagesByTweetId(id);

        List<Long> orphanMarkerIds = tweetMarkersClient.unlinkByTweetId(id);
        if (!orphanMarkerIds.isEmpty()) {
            markerClient.deleteMarkersByIds(orphanMarkerIds);
        }

        tweetRepository.deleteById(id);
    }

    @Transactional
    public void deleteTweetsByWriterId(Long writerId) {
        List<Long> tweetIds = tweetRepository.findAllByWriterId(writerId).stream()
                .map(Tweet::getId)
                .toList();

        for (Long tweetId : tweetIds) {
            deleteTweetById(tweetId);
        }
    }

    public WriterResponseTo getWriterByTweetId(Long id) {
        Tweet tweet = tweetRepository.findTweetById(id)
                .orElseThrow(() -> new TweetNotFoundException("Tweet not found"));

        return writerClient.getWriterById(tweet.getWriterId());
    }

    public List<MessageResponseTo> getMessagesByTweetId(Long id) {
        if (tweetRepository.findTweetById(id).isEmpty()) {
            throw new TweetNotFoundException("Tweet not found");
        }
        return messageClient.getAllMessagesByTweetId(id);
    }

    public List<MarkerResponseTo> getAllMarkersByTweetId(Long id) {
        if (tweetRepository.findTweetById(id).isEmpty()) {
            throw new TweetNotFoundException("Tweet not found");
        }

        List<Long> markerIds = tweetMarkersClient.getMarkersByTweetId(id);
        if (markerIds.isEmpty()) {
            return List.of();
        }

        return markerClient.getMarkersByIds(markerIds);
    }

    private void validateWriterExists(Long writerId) {
        try {
            writerClient.getWriterById(writerId);
        } catch (HttpClientErrorException.NotFound e) {
            throw new WriterNotFoundException("Writer not found");
        }
    }

    private Tweet toEntity(TweetRequestTo request) {
        return Tweet.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .writerId(request.getWriterId())
                .build();
    }

    private TweetResponseTo toDto(Tweet entity) {
        return TweetResponseTo.builder()
                .id(entity.getId())
                .writerId(entity.getWriterId())
                .title(entity.getTitle())
                .content(entity.getContent())
                .created(entity.getCreated())
                .build();
    }
}
