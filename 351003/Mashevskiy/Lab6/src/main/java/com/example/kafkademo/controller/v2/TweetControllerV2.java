package com.example.kafkademo.controller.v2;

import com.example.kafkademo.dto.request.TweetRequestDto;
import com.example.kafkademo.dto.response.TweetResponseDto;
import com.example.kafkademo.entity.Creator;
import com.example.kafkademo.entity.Marker;
import com.example.kafkademo.entity.Note;
import com.example.kafkademo.entity.Tweet;
import com.example.kafkademo.service.CreatorService;
import com.example.kafkademo.service.MarkerService;
import com.example.kafkademo.service.TweetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v2.0/tweets")
public class TweetControllerV2 {

    @Autowired
    private TweetService tweetService;

    @Autowired
    private CreatorService creatorService;

    @Autowired
    private MarkerService markerService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    public List<TweetResponseDto> getAll() {
        return tweetService.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    public ResponseEntity<TweetResponseDto> getById(@PathVariable Long id) {
        return tweetService.findById(id)
                .map(this::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/by-creator/{creatorId}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('CUSTOMER') and #creatorId == authentication.principal.id)")
    public List<TweetResponseDto> getByCreatorId(@PathVariable Long creatorId) {
        return tweetService.findByCreatorId(creatorId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('CUSTOMER')")
    public List<TweetResponseDto> getMyTweets() {
        String login = SecurityContextHolder.getContext().getAuthentication().getName();
        Creator creator = creatorService.findByLogin(login).get();
        return tweetService.findByCreatorId(creator.getId()).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    public ResponseEntity<TweetResponseDto> create(@Valid @RequestBody TweetRequestDto request) {
        Tweet tweet = new Tweet();
        tweet.setContent(request.getContent());

        if (request.getCreatorId() != null) {
            creatorService.findById(request.getCreatorId())
                    .ifPresent(tweet::setCreator);
        } else {
            String login = SecurityContextHolder.getContext().getAuthentication().getName();
            Creator creator = creatorService.findByLogin(login).get();
            tweet.setCreator(creator);
        }

        if (request.getMarkerIds() != null && !request.getMarkerIds().isEmpty()) {
            List<Marker> markers = request.getMarkerIds().stream()
                    .map(markerService::findById)
                    .filter(java.util.Optional::isPresent)
                    .map(java.util.Optional::get)
                    .collect(Collectors.toList());
            tweet.setMarkers(markers);
        }

        Tweet saved = tweetService.save(tweet);
        return ResponseEntity.status(HttpStatus.CREATED).body(toDto(saved));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('CUSTOMER') and @tweetService.findById(#id).get().creator.login == authentication.name)")
    public ResponseEntity<TweetResponseDto> update(@PathVariable Long id, @Valid @RequestBody TweetRequestDto request) {
        if (!tweetService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }

        Tweet tweet = tweetService.findById(id).get();
        tweet.setContent(request.getContent());

        if (request.getMarkerIds() != null) {
            List<Marker> markers = request.getMarkerIds().stream()
                    .map(markerService::findById)
                    .filter(java.util.Optional::isPresent)
                    .map(java.util.Optional::get)
                    .collect(Collectors.toList());
            tweet.setMarkers(markers);
        }

        Tweet updated = tweetService.save(tweet);
        return ResponseEntity.ok(toDto(updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('CUSTOMER') and @tweetService.findById(#id).get().creator.login == authentication.name)")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!tweetService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        tweetService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private TweetResponseDto toDto(Tweet tweet) {
        TweetResponseDto dto = new TweetResponseDto();
        dto.setId(tweet.getId());
        dto.setContent(tweet.getContent());
        dto.setCreatedAt(tweet.getCreatedAt());
        dto.setModifiedAt(tweet.getModifiedAt());
        dto.setCreatorId(tweet.getCreator() != null ? tweet.getCreator().getId() : null);
        dto.setNoteIds(tweet.getNotes() != null ?
                tweet.getNotes().stream().map(Note::getId).collect(Collectors.toList()) : null);
        dto.setMarkerIds(tweet.getMarkers() != null ?
                tweet.getMarkers().stream().map(Marker::getId).collect(Collectors.toList()) : null);
        return dto;
    }
}