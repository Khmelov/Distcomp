package com.example.labrest.controller;

import com.example.labrest.dto.*;
import com.example.labrest.service.AppService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1.0")
public class MainController {
    private final AppService service;
    public MainController(AppService service) { this.service = service; }

    @PostMapping("/creators") public ResponseEntity<CreatorResponseTo> createCreator(@Valid @RequestBody CreatorRequestTo req) {
        return ResponseEntity.status(201).body(service.createCreator(req));
    }
    @PostMapping("/topics") public ResponseEntity<TopicResponseTo> createTopic(@Valid @RequestBody TopicRequestTo req) {
        return ResponseEntity.status(201).body(service.createTopic(req));
    }
    @PostMapping("/markers") public ResponseEntity<MarkerResponseTo> createMarker(@Valid @RequestBody MarkerRequestTo req) {
        return ResponseEntity.status(201).body(service.createMarker(req));
    }
    @PostMapping("/notices") public ResponseEntity<NoticeResponseTo> createNotice(@Valid @RequestBody NoticeRequestTo req) {
        return ResponseEntity.status(201).body(service.createNotice(req));
    }

    @GetMapping("/creators") public List<CreatorResponseTo> getCreators() { return service.getAllCreators(); }
    @GetMapping("/topics") public List<TopicResponseTo> getTopics() { return service.getAllTopics(); }
    @GetMapping("/markers") public List<MarkerResponseTo> getMarkers() { return service.getAllMarkers(); }
    @GetMapping("/notices") public List<NoticeResponseTo> getNotices() { return service.getAllNotices(); }

    @GetMapping("/creators/{id}") public CreatorResponseTo getCreator(@PathVariable Long id) { return service.getCreator(id); }
    @GetMapping("/topics/{id}") public TopicResponseTo getTopic(@PathVariable Long id) { return service.getTopic(id); }
    @GetMapping("/markers/{id}") public MarkerResponseTo getMarker(@PathVariable Long id) { return service.getMarker(id); }
    @GetMapping("/notices/{id}") public NoticeResponseTo getNotice(@PathVariable Long id) { return service.getNotice(id); }

    @PutMapping("/creators/{id}") public CreatorResponseTo updateCreator(@PathVariable Long id, @Valid @RequestBody CreatorRequestTo req) {
        return service.updateCreator(id, req);
    }
    @PutMapping("/topics/{id}") public TopicResponseTo updateTopic(@PathVariable Long id, @Valid @RequestBody TopicRequestTo req) {
        return service.updateTopic(id, req);
    }
    @PutMapping("/markers/{id}") public MarkerResponseTo updateMarker(@PathVariable Long id, @Valid @RequestBody MarkerRequestTo req) {
        return service.updateMarker(id, req);
    }
    @PutMapping("/notices/{id}") public NoticeResponseTo updateNotice(@PathVariable Long id, @Valid @RequestBody NoticeRequestTo req) {
        return service.updateNotice(id, req);
    }

    @DeleteMapping("/creators/{id}") public ResponseEntity<Void> deleteCreator(@PathVariable Long id) {
        service.deleteCreator(id); return ResponseEntity.noContent().build();
    }
    @DeleteMapping("/topics/{id}") public ResponseEntity<Void> deleteTopic(@PathVariable Long id) {
        service.deleteTopic(id); return ResponseEntity.noContent().build();
    }
    @DeleteMapping("/markers/{id}") public ResponseEntity<Void> deleteMarker(@PathVariable Long id) {
        service.deleteMarker(id); return ResponseEntity.noContent().build();
    }
    @DeleteMapping("/notices/{id}") public ResponseEntity<Void> deleteNotice(@PathVariable Long id) {
        service.deleteNotice(id); return ResponseEntity.noContent().build();
    }

    @GetMapping("/topics/{id}/creator") public CreatorResponseTo getCreatorByTopic(@PathVariable Long id) { return service.getCreatorByTopicId(id); }
    @GetMapping("/topics/{id}/markers") public List<MarkerResponseTo> getMarkersByTopic(@PathVariable Long id) { return service.getMarkersByTopicId(id); }
    @GetMapping("/topics/{id}/notices") public List<NoticeResponseTo> getNoticesByTopic(@PathVariable Long id) { return service.getNoticesByTopicId(id); }
}