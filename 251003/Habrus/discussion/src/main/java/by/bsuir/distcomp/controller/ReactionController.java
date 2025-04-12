package by.bsuir.distcomp.controller;

import by.bsuir.distcomp.dto.request.ReactionRequestTo;
import by.bsuir.distcomp.dto.response.ReactionResponseTo;
import by.bsuir.distcomp.service.ReactionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1.0/reactions")
public class ReactionController {
    private final ReactionService reactionService;

    public ReactionController(ReactionService reactionService) {
        this.reactionService = reactionService;
    }

    @GetMapping
    ResponseEntity<List<ReactionResponseTo>> getAllReactions() {
        return new ResponseEntity<>(reactionService.getAllReactions(), HttpStatus.OK);
    }

    @GetMapping(value = "/{id:\\d+}")
    ResponseEntity<ReactionResponseTo> getReactionById(@PathVariable Long id,
                                                       @RequestHeader(name = "X-Country", defaultValue = "Belarus") String country){
        return new ResponseEntity<>(reactionService.getReactionById(country, id), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<ReactionResponseTo> createReaction(@RequestBody @Valid ReactionRequestTo reactionRequestTo,
                                                             @RequestHeader(name = "X-Country", defaultValue = "Belarus") String country) {
        return new ResponseEntity<>(reactionService.createReaction(country, reactionRequestTo), HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<ReactionResponseTo> updateReaction(@RequestBody @Valid ReactionRequestTo reactionRequestTo,
                                                             @RequestHeader(name = "X-Country", defaultValue = "Belarus") String country) {
        return new ResponseEntity<>(reactionService.updateReaction(country, reactionRequestTo), HttpStatus.OK);
    }

    @DeleteMapping(value = "/{id:\\d+}")
    public ResponseEntity<ReactionResponseTo> deleteReaction(@PathVariable Long id,
                                                             @RequestHeader(name = "X-Country", defaultValue = "Belarus") String country) {
        reactionService.deleteReaction(country, id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
