package com.distcomp.controller;


import com.distcomp.dto.note.NoteCreateRequest;
import com.distcomp.dto.note.NotePatchRequest;
import com.distcomp.dto.note.NoteResponseDto;
import com.distcomp.dto.note.NoteUpdateRequest;
import com.distcomp.service.NoteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1.0/notes")
@RequiredArgsConstructor
public class LegacyNoteController {

    private final NoteService noteService;

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<NoteResponseDto> getById(@PathVariable Long id) {
        return noteService.findById(id);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Flux<NoteResponseDto> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return noteService.findAll(page, size);
    }

    @GetMapping(params = "topicId")
    @ResponseStatus(HttpStatus.OK)
    public Flux<NoteResponseDto> getByTopicId(
            @RequestParam Long topicId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return noteService.findAllByTopicId(topicId, page, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<NoteResponseDto> create(@Valid @RequestBody NoteCreateRequest request) {
        return noteService.create(request);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<NoteResponseDto> update(@PathVariable Long id,
                                        @Valid @RequestBody NoteUpdateRequest request) {
        return noteService.update(id, request);
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<NoteResponseDto> patch(@PathVariable Long id,
                                       @RequestBody NotePatchRequest request) {
        return noteService.patch(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> delete(@PathVariable Long id) {
        return noteService.delete(id);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteByTopicId(@RequestParam Long topicId) {
        return noteService.deleteByTopicId(topicId);
    }
}