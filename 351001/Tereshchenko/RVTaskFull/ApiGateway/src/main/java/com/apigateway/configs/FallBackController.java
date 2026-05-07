package com.apigateway.configs;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

@RestController
public class FallBackController {

    @GetMapping("/fallback/writers")
    public ResponseEntity<List<String>> writersFallBack() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Collections.singletonList("Writer service is unavailable, please try again later"));
    }
    @GetMapping("/fallback/tweets")
    public ResponseEntity<List<String>> tweetsFallBack() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Collections.singletonList("Tweet service is unavailable, please try again later"));
    }
    @GetMapping("/fallback/messages")
    public ResponseEntity<List<String>> messagesFallBack() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Collections.singletonList("Message service is unavailable, please try again later"));
    }
    @GetMapping("/fallback/markers")
    public ResponseEntity<List<String>> markersFallBack() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Collections.singletonList("Marker service is unavailable, please try again later"));
    }
    @GetMapping("/fallback/tweet-markers")
    public ResponseEntity<List<String>> tweetMarkersFallBack() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Collections.singletonList("Tweet-markers service is unavailable, please try again later"));
    }
}
