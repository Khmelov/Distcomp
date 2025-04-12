package by.ryabchikov.tweet_service.controller;

import by.ryabchikov.tweet_service.dto.creator.CreatorRequestTo;
import by.ryabchikov.tweet_service.dto.creator.CreatorResponseTo;
import by.ryabchikov.tweet_service.dto.creator.CreatorUpdateRequestTo;
import by.ryabchikov.tweet_service.service.CreatorService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1.0/creators")
public class CreatorController {
    private final CreatorService creatorService;

    @GetMapping
    public ResponseEntity<List<CreatorResponseTo>> readAll() {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(creatorService.readAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CreatorResponseTo> readById(@PathVariable("id") @Valid @NotNull Long id) {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(creatorService.readById(id));
    }

    @PostMapping
    public ResponseEntity<CreatorResponseTo> create(@Valid @RequestBody CreatorRequestTo creatorRequestTo) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .contentType(MediaType.APPLICATION_JSON)
                .body(creatorService.create(creatorRequestTo));
    }

    @PutMapping
    public ResponseEntity<CreatorResponseTo> update(@Valid @RequestBody CreatorUpdateRequestTo creatorUpdateRequestTo) {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(creatorService.update(creatorUpdateRequestTo));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable("id") @Valid @NotNull Long id) {
        creatorService.deleteById(id);
        return ResponseEntity.noContent()
                .build();
    }
}
