package com.distcomp.controller.note;

import com.distcomp.dto.note.NoteCreateRequest;
import com.distcomp.dto.note.NotePatchRequest;
import com.distcomp.dto.note.NoteResponseDto;
import com.distcomp.dto.note.NoteUpdateRequest;
import com.distcomp.service.note.NoteProxyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@RestController
@RequestMapping("/api/v1.0/notes")
@RequiredArgsConstructor
public class NoteController {
    private final NoteProxyService proxyService;

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<NoteResponseDto> getById(@PathVariable Long id) {
        return proxyService.getNoteById(id);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Flux<NoteResponseDto> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return proxyService.getAllNotes(page, size);
    }

    @GetMapping(params = "topicId")
    @ResponseStatus(HttpStatus.OK)
    public Flux<NoteResponseDto> getByTopicId(
            @RequestParam Long topicId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return proxyService.getNotesByTopicId(topicId, page, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<NoteResponseDto> create(@Valid @RequestBody NoteCreateRequest request) {
        return proxyService.createNote(request);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<NoteResponseDto> update(@PathVariable Long id,
                                        @Valid @RequestBody NoteUpdateRequest request) {
        return proxyService.updateNote(id, request);
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<NoteResponseDto> patch(@PathVariable Long id,
                                       @RequestBody NotePatchRequest request) {
        return proxyService.patchNote(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> delete(@PathVariable Long id) {
        return proxyService.deleteNote(id);
    }
}