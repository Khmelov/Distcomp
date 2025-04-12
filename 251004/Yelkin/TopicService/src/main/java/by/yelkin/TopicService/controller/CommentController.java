package by.yelkin.TopicService.controller;

import by.yelkin.TopicService.dto.comment.CommentRq;
import by.yelkin.TopicService.dto.comment.CommentRs;
import by.yelkin.TopicService.dto.comment.CommentUpdateRq;
import by.yelkin.TopicService.service.CommentService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1.0/comments")
public class CommentController {
    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<CommentRs> create(@Valid @RequestBody CommentRq rq) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(commentService.create(rq));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommentRs> readById(@Valid @NotNull @PathVariable("id") Long id) {
        return ResponseEntity.ok().body(commentService.readById(id));
    }

    @GetMapping
    public ResponseEntity<List<CommentRs>> readAll() {
        return ResponseEntity.ok().body(commentService.readAll());
    }

    @PutMapping
    public ResponseEntity<CommentRs> update(@Valid @RequestBody CommentUpdateRq rq) {
        return ResponseEntity.ok().body(commentService.update(rq));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@Valid @NotNull @PathVariable("id") Long id) {
        commentService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}