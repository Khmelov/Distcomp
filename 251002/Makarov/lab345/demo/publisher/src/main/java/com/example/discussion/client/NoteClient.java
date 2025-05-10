package com.example.discussion.client;

import com.example.discussion.dto.NoteRequestTo;
import com.example.discussion.dto.NoteResponseTo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
@Component
public class NoteClient {

    private final RestClient restClient;
    private final String baseUrl;

    public NoteClient(@Value("${discussion.service.url}") String baseUrl) {
        this.baseUrl = baseUrl;
        this.restClient = RestClient.builder()
                .defaultStatusHandler(
                        HttpStatusCode::isError,
                        (request, response) -> {
                            throw new RuntimeException("Discussion service error: " + response.getStatusCode());
                        })
                .build();
    }

    public List<NoteResponseTo> getAllNotes() {
        return restClient.get()
                .uri(baseUrl)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }

    public NoteResponseTo getNoteById(Long id) {
        return restClient.get()
                .uri(baseUrl + "/{id}", id)
                .retrieve()
                .body(NoteResponseTo.class);
    }

    public NoteResponseTo createNote(NoteRequestTo dto) {
        return restClient.post()
                .uri(baseUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .body(dto)
                .retrieve()
                .body(NoteResponseTo.class);
    }

    public NoteResponseTo updateNote(NoteRequestTo dto) {
        return restClient.put()
                .uri(baseUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .body(dto)
                .retrieve()
                .body(NoteResponseTo.class);
    }

    public void deleteNote(Long id) {
        restClient.delete()
                .uri(baseUrl + "/{id}", id)
                .retrieve()
                .toBodilessEntity();
    }
}
