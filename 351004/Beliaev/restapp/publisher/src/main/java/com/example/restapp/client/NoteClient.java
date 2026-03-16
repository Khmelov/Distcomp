package com.example.restapp.client;

import com.example.restapp.dto.request.NoteRequestTo;
import com.example.restapp.dto.response.NoteResponseTo;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
public class NoteClient {

    private final RestClient restClient;

    public NoteClient() {
        this.restClient = RestClient.builder()
                .baseUrl("http://localhost:24130/api/v1.0/notes")
                .build();
    }

    public NoteResponseTo create(NoteRequestTo request) {
        return restClient.post()
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(NoteResponseTo.class);
    }

    public List<NoteResponseTo> getAll() {
        return restClient.get()
                .retrieve()
                .body(new ParameterizedTypeReference<List<NoteResponseTo>>() {});
    }

    public NoteResponseTo getById(Long id) {
        return restClient.get()
                .uri("/{id}", id)
                .retrieve()
                .body(NoteResponseTo.class);
    }

    public NoteResponseTo update(Long id, NoteRequestTo request) {
        return restClient.put()
                .uri("/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(NoteResponseTo.class);
    }

    public void delete(Long id) {
        restClient.delete()
                .uri("/{id}", id)
                .retrieve()
                .onStatus(status -> status.value() == 404, (request, response) -> {
                    // Если микросервис вернул 404, выбрасываем свою ошибку,
                    // которую потом поймает GlobalExceptionHandler
                    throw new com.example.restapp.exception.EntityNotFoundException("Note not found with id: " + id);
                })
                .toBodilessEntity();
    }
}