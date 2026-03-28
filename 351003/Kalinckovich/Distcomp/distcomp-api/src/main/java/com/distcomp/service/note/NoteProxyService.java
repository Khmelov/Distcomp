package com.distcomp.service.note;

import com.distcomp.dto.note.NoteCreateRequest;
import com.distcomp.dto.note.NotePatchRequest;
import com.distcomp.dto.note.NoteResponseDto;
import com.distcomp.dto.note.NoteUpdateRequest;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class NoteProxyService {

    private final WebClient webClient;

    public NoteProxyService(@Qualifier("noteServiceWebClient") final WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<NoteResponseDto> getNoteById(final Long id) {
        return webClient.get()
                .uri("/api/v1.0/notes/{id}", id)
                .retrieve()
                .bodyToMono(NoteResponseDto.class);
    }

    public Flux<NoteResponseDto> getAllNotes(final int page, final int size) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1.0/notes")
                        .queryParam("page", page)
                        .queryParam("size", size)
                        .build())
                .retrieve()
                .bodyToFlux(NoteResponseDto.class);
    }

    public Flux<NoteResponseDto> getNotesByTopicId(final Long topicId, final int page, final int size) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1.0/notes")
                        .queryParam("topicId", topicId)
                        .queryParam("page", page)
                        .queryParam("size", size)
                        .build())
                .retrieve()
                .bodyToFlux(NoteResponseDto.class);
    }

    public Mono<NoteResponseDto> createNote(final NoteCreateRequest request) {
        return webClient.post()
                .uri("/api/v1.0/notes")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(NoteResponseDto.class);
    }

    public Mono<NoteResponseDto> updateNote(final Long id, final NoteUpdateRequest request) {
        return webClient.put()
                .uri("/api/v1.0/notes/{id}", id)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(NoteResponseDto.class);
    }

    public Mono<NoteResponseDto> patchNote(final Long id, final NotePatchRequest request) {
        return webClient.patch()
                .uri("/api/v1.0/notes/{id}", id)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(NoteResponseDto.class);
    }

    public Mono<Void> deleteNotesByTopicId(final Long topicId) {
        return webClient.delete()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1.0/notes")
                        .queryParam("topicId", topicId)
                        .build())
                .retrieve()
                .bodyToMono(Void.class);
    }

    public Mono<Void> deleteNote(final Long id) {
        return webClient.delete()
                .uri("/api/v1.0/notes/{id}", id)
                .retrieve()
                .bodyToMono(Void.class);
    }
}