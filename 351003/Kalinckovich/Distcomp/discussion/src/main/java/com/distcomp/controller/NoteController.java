package com.distcomp.controller;

import com.distcomp.dto.note.NoteCreateRequest;
import com.distcomp.dto.note.NotePatchRequest;
import com.distcomp.dto.note.NoteResponseDto;
import com.distcomp.dto.note.NoteUpdateRequest;
import com.distcomp.service.NoteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@RestController("cassandraNoteController")
@RequestMapping("/api/v1.0/topics/{topicId}/notes")
@RequiredArgsConstructor
public class NoteController {

    private final NoteService noteService;

    @GetMapping("/{noteId}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<NoteResponseDto> getById(@PathVariable final Long topicId,
                                         @PathVariable final long noteId) {
        return noteService.findById(topicId, noteId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Flux<NoteResponseDto> getAllByTopic(
            @PathVariable final Long topicId,
            @RequestParam(defaultValue = "0") final int page,
            @RequestParam(defaultValue = "10") final int size) {
        return noteService.findAllByTopicId(topicId, page, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<NoteResponseDto> create(@PathVariable final Long topicId,
                                        @Valid @RequestBody final NoteCreateRequest request) {
        
        request.setTopicId(topicId);  
        return noteService.create(request);
    }

    @PutMapping("/{noteId}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<NoteResponseDto> update(@PathVariable final Long topicId,
                                        @PathVariable final long noteId,
                                        @Valid @RequestBody final NoteUpdateRequest request) {
        return noteService.update(topicId, noteId, request);
    }

    @PatchMapping("/{noteId}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<NoteResponseDto> patch(@PathVariable final Long topicId,
                                       @PathVariable final long noteId,
                                       @RequestBody final NotePatchRequest request) {
        return noteService.patch(topicId, noteId, request);
    }

    @DeleteMapping("/{noteId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> delete(@PathVariable final Long topicId,
                             @PathVariable final long noteId) {
        return noteService.delete(topicId, noteId);
    }
}