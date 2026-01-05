package com.publisher.controller;

import com.publisher.dto.request.NoteRequestTo;
import com.publisher.kafka.NoteConsumer;
import com.publisher.kafka.NoteProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.kafka.support.SendResult;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/api/v1.0/notes")
public class NoteController {
	
	private final WebClient webClient;
	
	private final NoteProducer noteProducer;
    private final NoteConsumer noteConsumer;

	
	public NoteController(@Value("${discussion.api.url:http://localhost:24130}")
						  String baseUrl,
						  NoteProducer noteProducer,
						  NoteConsumer noteConsumer) {
		this.webClient = WebClient.builder()
			.baseUrl(baseUrl)
			.build();
		this.noteProducer = noteProducer;
		this.noteConsumer = noteConsumer;
	}
	
    @PostMapping
    public Mono<ResponseEntity<Object>> createNote(@RequestBody NoteRequestTo request) {
		try {
			if (request.getId() == null) {
				request.setId(java.util.concurrent.ThreadLocalRandom.current().nextLong(2000L));
			}
			
			CompletableFuture<SendResult<String, String>> future =
				noteProducer.sendNoteForModeration(request);
				
			future.get(1, TimeUnit.SECONDS);
			
			return webClient.post()
					.uri("/api/v1.0/notes")
					.bodyValue(request)
					.retrieve()
					.toEntity(Object.class);
		} catch (Exception e) {
            return Mono.just(ResponseEntity.internalServerError()
                    .body("Error: " + e.getMessage()));
        }
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
		
		NoteRequestTo result = noteConsumer.getModerationResult(String.valueOf(id));
		
        return webClient.get()
                .uri("/api/v1.0/notes/{id}", id)
                .retrieve()
                .toEntity(Object.class);
    }
	
	@PutMapping("/{id}")
    public Mono<ResponseEntity<Object>> updateNote(@PathVariable Long id, @RequestBody NoteRequestTo request) {
		try {
			CompletableFuture<SendResult<String, String>> future =
				noteProducer.sendNoteForModeration(request);
				
			future.get(1, TimeUnit.SECONDS);
			
			return webClient.put()
					.uri("/api/v1.0/notes/{id}", id)
					.bodyValue(request)
					.retrieve()
					.toEntity(Object.class);
		} catch (Exception e) {
            return Mono.just(ResponseEntity.internalServerError()
                    .body("Error: " + e.getMessage()));
        }
    }
	
	@DeleteMapping("/{id}")
	public Mono<ResponseEntity<Void>> delete(@PathVariable Long id) {
		
		NoteRequestTo result = noteConsumer.getModerationResult(String.valueOf(id));
		
		return webClient.delete()
                .uri("/api/v1.0/notes/{id}", id)
                .retrieve()
                .toEntity(Void.class);
	}
}