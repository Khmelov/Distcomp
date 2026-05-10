package com.example.task361.controller.v2;

import com.example.task361.domain.dto.request.TweetRequestTo;
import com.example.task361.domain.dto.response.TweetResponseTo;
import com.example.task361.service.TweetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v2.0/tweets")
@RequiredArgsConstructor
public class TweetV2Controller {
    private final TweetService tweetService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN') or (hasRole('CUSTOMER') and @ownershipService.isCurrentAuthorId(#request.authorId))")
    public TweetResponseTo create(@Valid @RequestBody TweetRequestTo request) {
        return tweetService.create(request);
    }

    @GetMapping
    public List<TweetResponseTo> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return tweetService.findAll(page, size);
    }

    @GetMapping("/{id}")
    public TweetResponseTo findById(@PathVariable Long id) {
        return tweetService.findById(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('CUSTOMER') and @ownershipService.isTweetOwner(#id))")
    public TweetResponseTo update(@PathVariable Long id, @Valid @RequestBody TweetRequestTo request) {
        request.setId(id);
        return tweetService.update(request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN') or (hasRole('CUSTOMER') and @ownershipService.isTweetOwner(#id))")
    public void deleteById(@PathVariable Long id) {
        tweetService.deleteById(id);
    }
}
