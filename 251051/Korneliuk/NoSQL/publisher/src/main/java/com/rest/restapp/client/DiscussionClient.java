package com.rest.restapp.client;

import com.rest.restapp.dto.request.NoteRequestToDto;
import com.rest.restapp.dto.response.NoteResponseTo;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Component
public class DiscussionClient {

    private final WebClient webClient;

    public DiscussionClient() {
        this.webClient = WebClient.builder()
                .baseUrl("http://localhost:24130/api/v1.0")
                .build();
    }

    public NoteResponseTo createNote(NoteRequestToDto request) {
        return webClient.post()
                .uri("/notes")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(NoteResponseTo.class)
                .block();
    }

    public List<NoteResponseTo> getAll() {
        return webClient.get()
                .uri("/notes")
                .retrieve()
                .bodyToFlux(NoteResponseTo.class)
                .collectList()
                .block();
    }

    public NoteResponseTo getById(Long id) {
        return webClient.get()
                .uri("/notes/{id}", id)
                .retrieve()
                .bodyToMono(NoteResponseTo.class)
                .block();
    }

    public NoteResponseTo update(Long id, NoteRequestToDto request) {
        return webClient.put()
                .uri("/notes/{id}", id)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(NoteResponseTo.class)
                .block();
    }

    public List<NoteResponseTo> getNotesByIssueId(Long issueId) {
        return webClient.get()
                .uri("/notes/issue/" + issueId)
                .retrieve()
                .bodyToFlux(NoteResponseTo.class)
                .collectList()
                .block();
    }
    
    public ResponseEntity<Void> deleteNote(Long noteId) {
        return webClient.delete()
                .uri("/notes/{id}", noteId)
                .retrieve()
                .toBodilessEntity()
                .block();
    }
}
