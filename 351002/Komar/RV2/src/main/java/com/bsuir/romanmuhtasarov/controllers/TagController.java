package com.bsuir.romanmuhtasarov.controllers;

import com.bsuir.romanmuhtasarov.domain.request.TagRequestTo;
import com.bsuir.romanmuhtasarov.domain.response.TagResponseTo;
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
import com.bsuir.romanmuhtasarov.serivces.TagService;

import java.util.List;

@RestController
@RequestMapping("/tags")
public class TagController {
    private final TagService tagService;

    @Autowired
    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    @PostMapping
    public ResponseEntity<TagResponseTo> createComment(@RequestBody TagRequestTo tagRequestTo) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tagService.create(tagRequestTo));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TagResponseTo> findCommentById(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(tagService.findTagById(id));
    }

    @GetMapping
    public ResponseEntity<List<TagResponseTo>> findAllComments() {
        return ResponseEntity.status(HttpStatus.OK).body(tagService.read());
    }

    @PutMapping
    public ResponseEntity<TagResponseTo> updateComment(@RequestBody TagRequestTo tagRequestTo) {
        return ResponseEntity.status(HttpStatus.OK).body(tagService.update(tagRequestTo));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Long> deleteCommentById(@PathVariable Long id) {
        tagService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(id);
    }
}
