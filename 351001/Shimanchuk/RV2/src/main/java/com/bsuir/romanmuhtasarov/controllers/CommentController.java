package com.bsuir.romanmuhtasarov.controllers;

import com.bsuir.romanmuhtasarov.domain.request.CommentRequestTo;
import com.bsuir.romanmuhtasarov.domain.response.CommentResponseTo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.bsuir.romanmuhtasarov.serivces.CommentService;

import java.util.List;

@RestController
@RequestMapping("/comments")
public class CommentController {
    private final CommentService commentService;

    @Autowired
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping
    public ResponseEntity<CommentResponseTo> createComment(@RequestBody CommentRequestTo commentRequestTo) {
        return ResponseEntity.status(HttpStatus.CREATED).body(commentService.create(commentRequestTo));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommentResponseTo> findCommentById(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(commentService.findCommentById(id));
    }

    @GetMapping
    public ResponseEntity<List<CommentResponseTo>> findAllComments() {
        return ResponseEntity.status(HttpStatus.OK).body(commentService.read());
    }

    @PutMapping
    public ResponseEntity<CommentResponseTo> updateComment(@RequestBody CommentRequestTo commentRequestTo) {
        return ResponseEntity.status(HttpStatus.OK).body(commentService.update(commentRequestTo));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Long> deleteCommentById(@PathVariable Long id) {
        commentService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(id);
    }
}
