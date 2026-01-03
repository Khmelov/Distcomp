package com.publisher.controller;

import com.publisher.dto.request.NoteRequestTo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1.0/notes")
public class NoteController {
	
	private final WebClient webClient;
	
	public NoteController(@Value("${discussion.api.url:http://localhost:24130}")
						  String baseUrl) {
		this.webClient = WebClient.create(baseUrl);
	}
	
    @PostMapping
    public Mono<ResponseEntity<Object>> createNote(@RequestBody NoteRequestTo request) {
		return webClient.post()
                .uri("/api/v1.0/notes")
                .bodyValue(request)
                .retrieve()
                .toEntity(Object.class);
    }
	
	@GetMapping
    public Mono<ResponseEntity<Object>> getNotes() {
        return webClient.get()
                .uri("/api/v1.0/notes")
                .retrieve()
                .toEntity(Object.class);
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Object>> getNotesById(@PathVariable Long id) {
        return webClient.get()
                .uri("/api/v1.0/notes/{id}", id)
                .retrieve()
                .toEntity(Object.class);
    }
	
	@PutMapping("/{id}")
    public Mono<ResponseEntity<Object>> updateNote(@PathVariable Long id, @RequestBody NoteRequestTo request) {
		return webClient.put()
                .uri("/api/v1.0/notes/{id}", id)
                .bodyValue(request)
                .retrieve()
                .toEntity(Object.class);
    }
	
	@DeleteMapping("/{id}")
	public Mono<ResponseEntity<Void>> delete(@PathVariable Long id) {
		return webClient.delete()
                .uri("/api/v1.0/notes/{id}", id)
                .retrieve()
                .toEntity(Void.class);
	}
}