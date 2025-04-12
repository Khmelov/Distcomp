package by.kopvzakone.distcomp.controllers;

import by.kopvzakone.distcomp.dto.ReactionRequestTo;
import by.kopvzakone.distcomp.dto.ReactionResponseTo;
import by.kopvzakone.distcomp.services.ReactionService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;
import java.util.Collection;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("api/v1.0/reactions")
@AllArgsConstructor
public class ReactionController {
    private final ReactionService serviceImpl;
    private final WebClient webClient;
    @GetMapping
    public Flux<ReactionResponseTo> getAll() {
        return webClient.get()
                .uri("/api/v1.0/reactions")
                .retrieve()
                .bodyToFlux(ReactionResponseTo.class);
    }

    @GetMapping("/{id}")
    public Mono<ReactionResponseTo> getById(@PathVariable Long id) {
        return webClient.get()
                .uri("/api/v1.0/reactions/{id}", id)
                .retrieve()
                .bodyToMono(ReactionResponseTo.class);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ReactionResponseTo> create(@RequestBody @Valid ReactionRequestTo request) {
        return webClient.post()
                .uri("/api/v1.0/reactions")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(ReactionResponseTo.class);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> delete(@PathVariable Long id) {
        return webClient.delete()
                .uri("/api/v1.0/reactions/{id}", id)
                .retrieve()
                .bodyToMono(Void.class);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public Mono<ReactionResponseTo> update(@RequestBody @Valid ReactionRequestTo request) {
        return webClient.put()
                .uri("/api/v1.0/reactions")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(ReactionResponseTo.class);
    }
}

