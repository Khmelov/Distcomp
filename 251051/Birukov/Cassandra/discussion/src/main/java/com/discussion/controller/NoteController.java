package com.discussion.controller;

import com.discussion.entity.Note;
import com.discussion.dto.request.NoteRequestTo;
import com.discussion.dto.request.NoteResponseTo;
import com.discussion.service.NoteService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1.0/notes")
@RequiredArgsConstructor
@Validated
public class NoteController {
    
    private final NoteService noteService;
    
	public NoteController(NoteService noteService) {
		this.noteService = noteService;
	}
	
    @PostMapping
    public ResponseEntity<NoteResponseTo> createNote(@RequestBody NoteRequestTo request) {
        Note note = noteService.createNote(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(noteService.NoteToResponse(note));
    }
    
    @GetMapping
    public ResponseEntity<List<NoteResponseTo>> getAllNotes() {
		return ResponseEntity.ok(noteService.NotesToResponses(noteService.getAllNotes()));
    }
    
	@GetMapping("/{id}")
    public ResponseEntity<NoteResponseTo> getNoteById(@PathVariable @NotNull Long id) {
		return ResponseEntity.ok(noteService.NoteToResponse(noteService.getNoteById(id)));
    }
	
    @GetMapping("/{country}/{tweetId}/{id}")
    public ResponseEntity<Note> getNote(@PathVariable @NotBlank String country,
										@PathVariable @NotNull Long tweetId,
										@PathVariable @NotNull Long id) {
        Note note = noteService.getNote(country, tweetId, id);
        return ResponseEntity.ok(note);
    }
    
    @GetMapping("/country/{country}")
    public ResponseEntity<List<Note>> getNotesByCountry(@PathVariable @NotBlank String country) {
        List<Note> notes = noteService.getNotesByCountry(country);
        return ResponseEntity.ok(notes);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<NoteResponseTo> updateNote(@PathVariable @NotNull Long id,
													 @RequestBody NoteRequestTo request) {
        Note note = noteService.updateNote(id, request);
		return ResponseEntity.ok(noteService.NoteToResponse(note));
    }
	
	@DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteNote(
            @PathVariable @NotNull Long id) {
        noteService.deleteNote(id);
        
        return ResponseEntity.noContent().build();
    }
}