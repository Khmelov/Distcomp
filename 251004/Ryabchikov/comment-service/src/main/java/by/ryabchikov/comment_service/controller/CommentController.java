package by.ryabchikov.comment_service.controller;

import by.ryabchikov.comment_service.dto.CommentRequestTo;
import by.ryabchikov.comment_service.dto.CommentResponseTo;
import by.ryabchikov.comment_service.dto.CommentUpdateRequestTo;
import by.ryabchikov.comment_service.service.CommentService;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1.0/comments")
public class CommentController {
    private final CommentService commentService;

    @GetMapping
    public ResponseEntity<List<CommentResponseTo>> readAll() {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(commentService.readAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommentResponseTo> readById(@PathVariable("id") @Valid @NotNull Long id) {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(commentService.readById(id));
    }

    @PostMapping
    public ResponseEntity<CommentResponseTo> create(@Valid @RequestBody CommentRequestTo commentRequestTo) {
        CommentResponseTo body = commentService.create(commentRequestTo);
        return ResponseEntity.status(HttpStatus.CREATED)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body);
    }

    @PutMapping
    public ResponseEntity<CommentResponseTo> update(@Valid @RequestBody CommentUpdateRequestTo commentUpdateRequestTo) {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(commentService.update(commentUpdateRequestTo));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable("id") @Valid @NotNull Long id) {
        commentService.deleteById(id);
        return ResponseEntity.noContent()
                .build();
    }
}
