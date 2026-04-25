package com.example.task361.controller.v2;

import com.example.task361.domain.dto.request.ReactionRequestTo;
import com.example.task361.domain.dto.response.ReactionResponseTo;
import com.example.task361.service.ReactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v2.0/reactions")
@RequiredArgsConstructor
public class ReactionV2Controller {
    private final ReactionService reactionService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN') or (hasRole('CUSTOMER') and @ownershipService.isTweetOwnerByTweetId(#request.tweetId))")
    public ReactionResponseTo create(@Valid @RequestBody ReactionRequestTo request) {
        return reactionService.create(request);
    }

    @GetMapping
    public List<ReactionResponseTo> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return reactionService.findAll(page, size);
    }

    @GetMapping("/{id}")
    public ReactionResponseTo findById(@PathVariable Long id) {
        return reactionService.findById(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('CUSTOMER') and @ownershipService.isTweetOwnerByTweetId(#request.tweetId))")
    public ReactionResponseTo update(@PathVariable Long id, @Valid @RequestBody ReactionRequestTo request) {
        request.setId(id);
        return reactionService.update(request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN') or (hasRole('CUSTOMER') and @ownershipService.isReactionOnOwnedTweet(#id))")
    public void deleteById(@PathVariable Long id) {
        reactionService.deleteById(id);
    }
}
