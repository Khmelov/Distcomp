package com.tweetmarkersservice.controllers;

import com.tweetmarkersservice.dtos.TweetMarkerRequestTo;
import com.tweetmarkersservice.dtos.TweetMarkersRequestByNameTo;
import com.tweetmarkersservice.dtos.TweetMarkersResponseTo;
import com.tweetmarkersservice.services.TweetMarkersService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1.0")
public class TweetMarkersController {

    private final TweetMarkersService tweetMarkersService;

    @PostMapping("/tweet-markers")
    public ResponseEntity<TweetMarkersResponseTo> linkTweetMarker(@RequestBody TweetMarkerRequestTo request) {
        return new ResponseEntity<>(tweetMarkersService.linkTweetMarker(request), HttpStatus.OK);
    }

    @PostMapping("/tweet-markers-all")
    public ResponseEntity<Void> linkTweetMarkers(@RequestBody List<TweetMarkersRequestByNameTo> request) {
        tweetMarkersService.linkTweetMarkersByNames(request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/tweet-markers/{tweetId}")
    public ResponseEntity<List<Long>> unlinkByTweetId(@PathVariable Long tweetId) {
        return ResponseEntity.ok(tweetMarkersService.unlinkByTweetId(tweetId));
    }

    @GetMapping("/tweet-markers/{tweetId}")
    public ResponseEntity<List<Long>> getMarkersByTweetId(@PathVariable Long tweetId) {
        return ResponseEntity.ok(tweetMarkersService.getMarkersByTweetId(tweetId));
    }
}
