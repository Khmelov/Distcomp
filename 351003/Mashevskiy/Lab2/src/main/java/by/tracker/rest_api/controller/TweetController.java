package by.tracker.rest_api.controller;

import by.tracker.rest_api.entity.Creator;
import by.tracker.rest_api.entity.Marker;
import by.tracker.rest_api.entity.Tweet;
import by.tracker.rest_api.repository.CreatorRepository;
import by.tracker.rest_api.repository.MarkerRepository;
import by.tracker.rest_api.repository.TweetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1.0/tweets")
public class TweetController {

    @Autowired
    private TweetRepository tweetRepository;

    @Autowired
    private CreatorRepository creatorRepository;

    @Autowired
    private MarkerRepository markerRepository;

    @GetMapping
    public List<Map<String, Object>> getAll() {
        return tweetRepository.findAll().stream()
                .map(this::toResponseMap)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getById(@PathVariable Long id) {
        return tweetRepository.findById(id)
                .map(tweet -> ResponseEntity.ok(toResponseMap(tweet)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Map<String, Object> payload) {
        try {
            Long creatorId = Long.valueOf(payload.get("creatorId").toString());
            String title = payload.get("title").toString();
            String content = payload.get("content").toString();

            if (title.length() < 2 || title.length() > 64) {
                Map<String, Object> error = new HashMap<>();
                error.put("errorMessage", "Title must be between 2 and 64 characters");
                error.put("errorCode", 40010);
                error.put("timestamp", System.currentTimeMillis());
                return ResponseEntity.badRequest().body(error);
            }
            if (content.length() < 4 || content.length() > 2048) {
                Map<String, Object> error = new HashMap<>();
                error.put("errorMessage", "Content must be between 4 and 2048 characters");
                error.put("errorCode", 40011);
                error.put("timestamp", System.currentTimeMillis());
                return ResponseEntity.badRequest().body(error);
            }

            if (tweetRepository.existsByTitle(title)) {
                Map<String, Object> error = new HashMap<>();
                error.put("errorMessage", "Tweet with this title already exists");
                error.put("errorCode", 40302);
                error.put("timestamp", System.currentTimeMillis());
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
            }

            Creator creator = creatorRepository.findById(creatorId).orElse(null);
            if (creator == null) {
                Map<String, Object> error = new HashMap<>();
                error.put("errorMessage", "Creator not found");
                error.put("errorCode", 40401);
                error.put("timestamp", System.currentTimeMillis());
                return ResponseEntity.badRequest().body(error);
            }

            Tweet tweet = new Tweet();
            tweet.setTitle(title);
            tweet.setContent(content);
            tweet.setCreator(creator);

            if (payload.containsKey("markers")) {
                List<String> markerNames = (List<String>) payload.get("markers");
                List<Marker> markers = new ArrayList<>();
                for (String markerName : markerNames) {
                    Marker marker = markerRepository.findByName(markerName).orElse(null);
                    if (marker == null) {
                        marker = new Marker();
                        marker.setName(markerName);
                        marker = markerRepository.save(marker);
                    }
                    markers.add(marker);
                }
                tweet.setMarkers(markers);
            }

            Tweet saved = tweetRepository.save(tweet);
            return ResponseEntity.status(HttpStatus.CREATED).body(toResponseMap(saved));

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("errorMessage", e.getMessage());
            error.put("errorCode", 40000);
            error.put("timestamp", System.currentTimeMillis());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Map<String, Object> payload) {
        try {
            Tweet existingTweet = tweetRepository.findById(id).orElse(null);
            if (existingTweet == null) {
                return ResponseEntity.notFound().build();
            }

            if (payload.containsKey("title")) {
                String title = payload.get("title").toString();
                if (title.length() < 2 || title.length() > 64) {
                    Map<String, Object> error = new HashMap<>();
                    error.put("errorMessage", "Title must be between 2 and 64 characters");
                    error.put("errorCode", 40010);
                    error.put("timestamp", System.currentTimeMillis());
                    return ResponseEntity.badRequest().body(error);
                }
                if (!title.equals(existingTweet.getTitle()) && tweetRepository.existsByTitle(title)) {
                    Map<String, Object> error = new HashMap<>();
                    error.put("errorMessage", "Tweet with this title already exists");
                    error.put("errorCode", 40302);
                    error.put("timestamp", System.currentTimeMillis());
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
                }
                existingTweet.setTitle(title);
            }

            if (payload.containsKey("content")) {
                String content = payload.get("content").toString();
                if (content.length() < 4 || content.length() > 2048) {
                    Map<String, Object> error = new HashMap<>();
                    error.put("errorMessage", "Content must be between 4 and 2048 characters");
                    error.put("errorCode", 40011);
                    error.put("timestamp", System.currentTimeMillis());
                    return ResponseEntity.badRequest().body(error);
                }
                existingTweet.setContent(content);
            }

            if (payload.containsKey("creatorId")) {
                Long creatorId = Long.valueOf(payload.get("creatorId").toString());
                Creator creator = creatorRepository.findById(creatorId).orElse(null);
                if (creator == null) {
                    Map<String, Object> error = new HashMap<>();
                    error.put("errorMessage", "Creator not found");
                    error.put("errorCode", 40401);
                    error.put("timestamp", System.currentTimeMillis());
                    return ResponseEntity.badRequest().body(error);
                }
                existingTweet.setCreator(creator);
            }

            Tweet saved = tweetRepository.save(existingTweet);
            return ResponseEntity.ok(toResponseMap(saved));

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("errorMessage", e.getMessage());
            error.put("errorCode", 40000);
            error.put("timestamp", System.currentTimeMillis());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!tweetRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        tweetRepository.deleteById(id);

        markerRepository.deleteOrphanMarkers();

        return ResponseEntity.noContent().build();
    }

    private Map<String, Object> toResponseMap(Tweet tweet) {
        Map<String, Object> response = new HashMap<>();
        response.put("id", tweet.getId());
        response.put("creatorId", tweet.getCreator() != null ? tweet.getCreator().getId() : null);
        response.put("title", tweet.getTitle());
        response.put("content", tweet.getContent());
        response.put("created", tweet.getCreated() != null ? tweet.getCreated().toString() : null);
        response.put("modified", tweet.getModified() != null ? tweet.getModified().toString() : null);
        response.put("markerIds", tweet.getMarkers() != null ?
                tweet.getMarkers().stream().map(Marker::getId).collect(Collectors.toList()) : new ArrayList<>());
        return response;
    }
}