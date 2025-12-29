package com.rest.controller;

import com.rest.dto.request.NoteRequestTo;
import com.rest.dto.response.NoteResponseTo;
import com.rest.service.NoteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1.0/notes")
public class NoteController {
    
	@Autowired
    private final NoteService noteService;
    
    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }
    
    @PostMapping
    public ResponseEntity<NoteResponseTo> create(@Valid @RequestBody NoteRequestTo request) {
        NoteResponseTo response = noteService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<NoteResponseTo> findById(@PathVariable Long id) {
        NoteResponseTo response = noteService.findById(id);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping
    public ResponseEntity<List<NoteResponseTo>> findAll() {
        List<NoteResponseTo> responses = noteService.findAll();
        return ResponseEntity.ok(responses);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<NoteResponseTo> update(@PathVariable Long id, 
                                                 @Valid @RequestBody NoteRequestTo request) {
        NoteResponseTo response = noteService.update(id, request);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        noteService.delete(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/tweet/{tweetId}")
    public ResponseEntity<List<NoteResponseTo>> findByTweetId(@PathVariable Long tweetId) {
        List<NoteResponseTo> responses = noteService.findByTweetId(tweetId);
        return ResponseEntity.ok(responses);
    }
}