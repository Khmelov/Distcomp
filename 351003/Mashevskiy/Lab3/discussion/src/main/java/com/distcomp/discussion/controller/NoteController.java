package com.distcomp.discussion.controller;

import com.distcomp.discussion.dto.NoteRequestDTO;
import com.distcomp.discussion.dto.NoteResponseDTO;
import com.distcomp.discussion.exception.NoteNotFoundException;
import com.distcomp.discussion.service.NoteService;
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
    private NoteService noteService;

    @GetMapping
    public ResponseEntity<List<NoteResponseDTO>> getAllNotes() {
        return ResponseEntity.ok(noteService.getAllNotes());
    }

    @GetMapping("/{id}")
    public ResponseEntity<NoteResponseDTO> getNoteById(@PathVariable Long id) {
        List<NoteResponseDTO> allNotes = noteService.getAllNotes();
        NoteResponseDTO found = allNotes.stream()
                .filter(note -> note.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new NoteNotFoundException("Note not found with id: " + id));
        return ResponseEntity.ok(found);
    }

    @GetMapping("/tweet/{tweetId}")
    public ResponseEntity<List<NoteResponseDTO>> getNotesByTweetId(@PathVariable Long tweetId) {
        return ResponseEntity.ok(noteService.getNotesByTweetId(tweetId));
    }

    @GetMapping("/{tweetId}/{id}")
    public ResponseEntity<NoteResponseDTO> getNote(@PathVariable Long tweetId, @PathVariable Long id) {
        return ResponseEntity.ok(noteService.getNote(tweetId, id));
    }

    @PostMapping
    public ResponseEntity<NoteResponseDTO> createNote(@Valid @RequestBody NoteRequestDTO request) {
        NoteResponseDTO created = noteService.createNote(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<NoteResponseDTO> updateNoteById(@PathVariable Long id,
                                                          @Valid @RequestBody NoteRequestDTO request) {
        List<NoteResponseDTO> allNotes = noteService.getAllNotes();
        NoteResponseDTO existing = allNotes.stream()
                .filter(note -> note.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new NoteNotFoundException("Note not found with id: " + id));
        return ResponseEntity.ok(noteService.updateNote(existing.getTweetId(), id, request));
    }

    @PutMapping("/{tweetId}/{id}")
    public ResponseEntity<NoteResponseDTO> updateNote(@PathVariable Long tweetId,
                                                      @PathVariable Long id,
                                                      @Valid @RequestBody NoteRequestDTO request) {
        return ResponseEntity.ok(noteService.updateNote(tweetId, id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNoteById(@PathVariable Long id) {
        List<NoteResponseDTO> allNotes = noteService.getAllNotes();
        NoteResponseDTO existing = allNotes.stream()
                .filter(note -> note.getId().equals(id))
                .findFirst()
                .orElse(null);

        if (existing == null) {
            return ResponseEntity.notFound().build();
        }

        noteService.deleteNote(existing.getTweetId(), id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{tweetId}/{id}")
    public ResponseEntity<Void> deleteNote(@PathVariable Long tweetId, @PathVariable Long id) {
        noteService.deleteNote(tweetId, id);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(NoteNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<String> handleNotFound(NoteNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }
}