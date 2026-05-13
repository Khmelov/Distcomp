package com.example.demo.labrest.controller;

import com.example.demo.labrest.dto.*;
import com.example.demo.labrest.exception.ForbiddenException;
import com.example.demo.labrest.service.AppService;
import com.example.demo.labrest.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v2.0")
@RequiredArgsConstructor
public class AuthController {

    private final AppService appService;
    private final AuthService authService;

    @PostMapping("/creators")
    public ResponseEntity<CreatorResponseTo> register(@Valid @RequestBody CreatorRequestTo req) {
        if (req.getLogin() == null || req.getLogin().isEmpty()) {
            throw new IllegalArgumentException("Login is required");
        }
        if (req.getPassword() == null || req.getPassword().isEmpty()) {
            throw new IllegalArgumentException("Password is required");
        }
        return ResponseEntity.status(201).body(appService.createCreator(req));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseTo> login(@Valid @RequestBody LoginRequestTo req) {
        String token = authService.authenticate(req.getLogin(), req.getPassword());
        return ResponseEntity.ok(LoginResponseTo.builder()
                .access_token(token)
                .token_type("Bearer")
                .build());
    }

    @GetMapping("/me")
    public ResponseEntity<CreatorResponseTo> getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String login = auth.getName();
        return ResponseEntity.ok(appService.getCreatorByLogin(login));
    }

    @GetMapping("/creators")
    public ResponseEntity<List<CreatorResponseTo>> getCreators(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(appService.getAllCreators(PageRequest.of(page, size, Sort.by("id"))));
    }

    @GetMapping("/creators/{id}")
    public ResponseEntity<CreatorResponseTo> getCreator(@PathVariable Long id) {
        return ResponseEntity.ok(appService.getCreator(id));
    }

    @PutMapping("/creators/{id}")
    @PreAuthorize("hasRole('ADMIN') or @authService.isCurrentUser(#id)")
    public ResponseEntity<CreatorResponseTo> updateCreator(
            @PathVariable Long id,
            @Valid @RequestBody CreatorRequestTo req) {
        return ResponseEntity.ok(appService.updateCreator(id, req));
    }

    @DeleteMapping("/creators/{id}")
    @PreAuthorize("hasRole('ADMIN') or @authService.isCurrentUser(#id)")
    public ResponseEntity<Void> deleteCreator(@PathVariable Long id) {
        appService.deleteCreator(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/topics")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<TopicResponseTo> createTopic(@Valid @RequestBody TopicRequestTo req) {
        if (!authService.isCurrentUserAdmin()) {
            Long currentId = authService.getCurrentUserId();
            if (!req.getCreatorId().equals(currentId)) {
                throw new ForbiddenException("You can only create topics for yourself");
            }
        }
        return ResponseEntity.status(201).body(appService.createTopic(req));
    }

    @GetMapping("/topics")
    public ResponseEntity<List<TopicResponseTo>> getTopics(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(appService.getAllTopics(PageRequest.of(page, size, Sort.by("id"))));
    }

    @GetMapping("/topics/{id}")
    public ResponseEntity<TopicResponseTo> getTopic(@PathVariable Long id) {
        return ResponseEntity.ok(appService.getTopic(id));
    }

    @PutMapping("/topics/{id}")
    @PreAuthorize("hasRole('ADMIN') or @appService.isTopicOwner(#id, authentication.name)")
    public ResponseEntity<TopicResponseTo> updateTopic(
            @PathVariable Long id,
            @Valid @RequestBody TopicRequestTo req) {
        return ResponseEntity.ok(appService.updateTopic(id, req));
    }

    @DeleteMapping("/topics/{id}")
    @PreAuthorize("hasRole('ADMIN') or @appService.isTopicOwner(#id, authentication.name)")
    public ResponseEntity<Void> deleteTopic(@PathVariable Long id) {
        appService.deleteTopicAndOrphanMarkers(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/markers")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MarkerResponseTo> createMarker(@Valid @RequestBody MarkerRequestTo req) {
        return ResponseEntity.status(201).body(appService.createMarker(req));
    }

    @GetMapping("/markers")
    public ResponseEntity<List<MarkerResponseTo>> getMarkers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(appService.getAllMarkers(PageRequest.of(page, size, Sort.by("id"))));
    }

    @GetMapping("/markers/{id}")
    public ResponseEntity<MarkerResponseTo> getMarker(@PathVariable Long id) {
        return ResponseEntity.ok(appService.getMarker(id));
    }

    @PutMapping("/markers/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MarkerResponseTo> updateMarker(
            @PathVariable Long id,
            @Valid @RequestBody MarkerRequestTo req) {
        return ResponseEntity.ok(appService.updateMarker(id, req));
    }

    @DeleteMapping("/markers/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteMarker(@PathVariable Long id) {
        appService.deleteMarker(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/notices")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<NoticeResponseTo> createNotice(@Valid @RequestBody NoticeRequestTo req) {
        return ResponseEntity.status(201).body(appService.createNotice(req));
    }

    @GetMapping("/notices")
    public ResponseEntity<List<NoticeResponseTo>> getNotices(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(appService.getAllNotices(PageRequest.of(page, size, Sort.by("id"))));
    }

    @GetMapping("/notices/{id}")
    public ResponseEntity<NoticeResponseTo> getNotice(@PathVariable Long id) {
        return ResponseEntity.ok(appService.getNotice(id));
    }

    @PutMapping("/notices/{id}")
    @PreAuthorize("hasRole('ADMIN') or @appService.isNoticeOwner(#id, authentication.name)")
    public ResponseEntity<NoticeResponseTo> updateNotice(
            @PathVariable Long id,
            @Valid @RequestBody NoticeRequestTo req) {
        return ResponseEntity.ok(appService.updateNotice(id, req));
    }

    @DeleteMapping("/notices/{id}")
    @PreAuthorize("hasRole('ADMIN') or @appService.isNoticeOwner(#id, authentication.name)")
    public ResponseEntity<Void> deleteNotice(@PathVariable Long id) {
        appService.deleteNotice(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/topics/{id}/creator")
    public ResponseEntity<CreatorResponseTo> getCreatorByTopic(@PathVariable Long id) {
        return ResponseEntity.ok(appService.getCreatorByTopicId(id));
    }

    @GetMapping("/topics/{id}/markers")
    public ResponseEntity<List<MarkerResponseTo>> getMarkersByTopic(@PathVariable Long id) {
        return ResponseEntity.ok(appService.getMarkersByTopicId(id));
    }

    @GetMapping("/topics/{id}/notices")
    public ResponseEntity<List<NoticeResponseTo>> getNoticesByTopic(@PathVariable Long id) {
        return ResponseEntity.ok(appService.getNoticesByTopicId(id));
    }

    @GetMapping("/topics/search")
    public ResponseEntity<List<TopicResponseTo>> searchTopics(
            @RequestParam(required = false) List<String> markerNames,
            @RequestParam(required = false) List<Long> markerIds,
            @RequestParam(required = false) String creatorLogin,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String content) {
        return ResponseEntity.ok(appService.getTopicsByFilters(markerNames, markerIds, creatorLogin, title, content));
    }
}