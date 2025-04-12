package by.bsuir.distcomp.controller;

import by.bsuir.distcomp.dto.request.ReactionRequestTo;
import by.bsuir.distcomp.dto.response.ReactionResponseTo;
import by.bsuir.distcomp.service.ReactionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1.0/reactions")
public class ReactionController {
    private final ReactionService reactionService;
    private final WebClient webClient;

    public ReactionController(ReactionService reactionService, WebClient webClient) {
        this.reactionService = reactionService;
        this.webClient = webClient;
    }

    @GetMapping
    ResponseEntity<List<ReactionResponseTo>> getAllReactions() {
        return new ResponseEntity<>(reactionService.getAllReactions(), HttpStatus.OK);
    }

    @GetMapping(value = "/{id:\\d+}")
    ResponseEntity<ReactionResponseTo> getReactionById(@PathVariable Long id) {
        return new ResponseEntity<>(reactionService.getReactionById(id), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<ReactionResponseTo> createReaction(@RequestBody @Valid ReactionRequestTo reactionRequestTo) {
        ReactionResponseTo reactionResponseTo = reactionService.createReaction(reactionRequestTo);
        sendPostRequest(reactionResponseTo);
        return new ResponseEntity<>(reactionResponseTo, HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<ReactionResponseTo> updateReaction(@RequestBody @Valid ReactionRequestTo reactionRequestTo) {
        ReactionResponseTo reactionResponseTo = reactionService.updateReaction(reactionRequestTo);
        sendPutRequest(reactionResponseTo);
        return new ResponseEntity<>(reactionService.updateReaction(reactionRequestTo), HttpStatus.OK);
    }

    @DeleteMapping(value = "/{id:\\d+}")
    public ResponseEntity<ReactionResponseTo> deleteReaction(@PathVariable Long id) {
        reactionService.deleteReaction(id);
        sendDeleteRequest(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    private final String url = "http://localhost:24130/api/v1.0/reactions";

    private void sendPostRequest(ReactionResponseTo body) {
        webClient.post()
                .uri(url)
                .bodyValue(body)
                .retrieve()
                .toBodilessEntity()
                .subscribe();
    }

    private void sendPutRequest(ReactionResponseTo body) {
        webClient.put()
                .uri(url)
                .bodyValue(body)
                .retrieve()
                .toBodilessEntity()
                .subscribe();
    }

    private void sendDeleteRequest(Long id) {
        webClient.delete()
                .uri(url + id.toString())
                .retrieve()
                .toBodilessEntity()
                .subscribe();
    }
}
