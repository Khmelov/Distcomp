package by.distcomp.app.controller;

import by.distcomp.app.dto.NoteRequestTo;
import by.distcomp.app.dto.NoteResponseTo;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import by.distcomp.app.service.NoteService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1.0/notes")
public class NoteController {
    private final NoteService noteService;

    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }
    @GetMapping
    public List<NoteResponseTo> getNotes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return noteService.getNotesPage(pageable);
    }
    @GetMapping("/{note-id}")
    public NoteResponseTo getNote(@PathVariable ("note-id") Long noteId){
        return  noteService.getNoteById(noteId);
    }
    @PostMapping
    public ResponseEntity<NoteResponseTo> createNote(@Valid @RequestBody NoteRequestTo request){
        NoteResponseTo createdNote = noteService.createNote(request);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdNote.id())
                .toUri();

        return ResponseEntity
                .created(location)
                .body(createdNote);
    }

    @PutMapping("/{note-id}")
    public NoteResponseTo updateNote(@PathVariable ("note-id") Long noteId, @Valid @RequestBody NoteRequestTo request){
        return  noteService.updateNote(noteId,request);
    }
    @DeleteMapping("/{note-id}")
    public ResponseEntity<Void> deleteNote(@PathVariable ("note-id") Long noteId){
        noteService.deleteNote(noteId);
        return ResponseEntity.noContent().build();
    }
}