package com.distcomp.controller.note;

import com.distcomp.dto.note.NoteCreateRequest;
import com.distcomp.dto.note.NotePatchRequest;
import com.distcomp.dto.note.NoteResponseDto;
import com.distcomp.dto.note.NoteUpdateRequest;
import com.distcomp.service.note.NoteProxyService;
import com.distcomp.service.security.AuthorizationService;
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
@RequestMapping("/api/v2.0/notes")
@RequiredArgsConstructor
public class NoteControllerV2 {

    private final NoteProxyService proxyService;
    private final AuthorizationService authorizationService;

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public Mono<NoteResponseDto> getById(@PathVariable final Long id) {
        return proxyService.getNoteById(id);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public Flux<NoteResponseDto> getAll(
            @RequestParam(defaultValue = "0") final int page,
            @RequestParam(defaultValue = "10") final int size) {
        return proxyService.getAllNotes(page, size);
    }

    @GetMapping(params = "topicId")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public Flux<NoteResponseDto> getByTopicId(
            @RequestParam final Long topicId,
            @RequestParam(defaultValue = "0") final int page,
            @RequestParam(defaultValue = "10") final int size) {
        return proxyService.getNotesByTopicId(topicId, page, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public Mono<NoteResponseDto> create(@Valid @RequestBody final NoteCreateRequest request) {
        return ReactiveSecurityContextHolder.getContext()
                .mapNotNull(SecurityContext::getAuthentication)
                .flatMap(_ -> proxyService.createNote(request));
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<NoteResponseDto> update(@PathVariable final Long id,
                                        @Valid @RequestBody final NoteUpdateRequest request) {
        return ReactiveSecurityContextHolder.getContext()
                .mapNotNull(SecurityContext::getAuthentication)
                .flatMap(auth -> {
                    final boolean isAdmin = auth.getAuthorities().stream()
                            .anyMatch(a -> Objects.equals(a.getAuthority(), "ROLE_ADMIN"));
                    final String login = auth.getName();

                    return authorizationService.isNoteOwner(id, login)
                            .flatMap(isOwner -> {
                                if (!isAdmin && !isOwner) {
                                    return Mono.error(new AccessDeniedException("Access denied"));
                                }
                                return proxyService.updateNote(id, request);
                            });
                });
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<NoteResponseDto> patch(@PathVariable final Long id,
                                       @RequestBody final NotePatchRequest request) {
        return ReactiveSecurityContextHolder.getContext()
                .mapNotNull(SecurityContext::getAuthentication)
                .flatMap(auth -> {
                    final boolean isAdmin = auth.getAuthorities().stream()
                            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
                    final String login = auth.getName();

                    return authorizationService.isNoteOwner(id, login)
                            .flatMap(isOwner -> {
                                if (!isAdmin && !isOwner) {
                                    return Mono.error(new AccessDeniedException("Access denied"));
                                }
                                return proxyService.patchNote(id, request);
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

                    return authorizationService.isNoteOwner(id, login)
                            .flatMap(isOwner -> {
                                if (!isAdmin && !isOwner) {
                                    return Mono.error(new AccessDeniedException("Access denied"));
                                }
                                return proxyService.deleteNote(id);
                            });
                });
    }
}