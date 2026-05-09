package com.distcomp.controller.topic;

import com.distcomp.dto.topic.TopicCreateRequest;
import com.distcomp.dto.topic.TopicPatchRequest;
import com.distcomp.dto.topic.TopicResponseDto;
import com.distcomp.dto.topic.TopicUpdateRequest;
import com.distcomp.service.security.AuthorizationService;
import com.distcomp.service.topic.TopicService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Objects;

@RestController
@RequestMapping("/api/v2.0/topics")
@RequiredArgsConstructor
public class TopicControllerV2 {

    private final TopicService topicService;
    private final AuthorizationService authorizationService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public Flux<TopicResponseDto> getAll(
            @RequestParam(defaultValue = "0") final int page,
            @RequestParam(defaultValue = "10") final int size) {
        return topicService.findAll(page, size);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public Mono<TopicResponseDto> getById(@PathVariable final Long id) {
        return topicService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public Mono<TopicResponseDto> create(@Valid @RequestBody final TopicCreateRequest request) {
        return ReactiveSecurityContextHolder.getContext()
                .mapNotNull(SecurityContext::getAuthentication)
                .flatMap(_ -> topicService.create(request));
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<TopicResponseDto> update(@PathVariable final Long id,
                                         @Valid @RequestBody final TopicUpdateRequest request) {
        return ReactiveSecurityContextHolder.getContext()
                .mapNotNull(SecurityContext::getAuthentication)
                .flatMap(auth -> {
                    final boolean isAdmin = auth.getAuthorities().stream()
                            .anyMatch(a -> Objects.equals(a.getAuthority(), "ROLE_ADMIN"));
                    final String login = auth.getName();

                    return authorizationService.isTopicOwner(id, login)
                            .flatMap(isOwner -> {
                                if (!isAdmin && !isOwner) {
                                    return Mono.error(new AccessDeniedException("Access denied"));
                                }
                                return topicService.update(id, request);
                            });
                });
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<TopicResponseDto> patch(@PathVariable final Long id,
                                        @RequestBody final TopicPatchRequest request) {
        return ReactiveSecurityContextHolder.getContext()
                .mapNotNull(SecurityContext::getAuthentication)
                .flatMap(auth -> {
                    final boolean isAdmin = auth.getAuthorities().stream()
                            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
                    final String login = auth.getName();

                    return authorizationService.isTopicOwner(id, login)
                            .flatMap(isOwner -> {
                                if (!isAdmin && !isOwner) {
                                    return Mono.error(new AccessDeniedException("Access denied"));
                                }
                                return topicService.patch(id, request);
                            });
                });
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> delete(@PathVariable final Long id) {
        return ReactiveSecurityContextHolder.getContext()
                .mapNotNull(SecurityContext::getAuthentication)
                .flatMap(auth -> {
                    final boolean isAdmin = auth.getAuthorities().stream()
                            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
                    final String login = auth.getName();

                    return authorizationService.isTopicOwner(id, login)
                            .flatMap(isOwner -> {
                                if (!isAdmin && !isOwner) {
                                    return Mono.error(new AccessDeniedException("Access denied"));
                                }
                                return topicService.delete(id);
                            });
                });
    }

    @GetMapping(params = "userId")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public Flux<TopicResponseDto> getByUserId(
            @RequestParam final Long userId,
            @RequestParam(defaultValue = "0") final int page,
            @RequestParam(defaultValue = "10") final int size) {
        return topicService.findByUserId(userId, page, size);
    }
}