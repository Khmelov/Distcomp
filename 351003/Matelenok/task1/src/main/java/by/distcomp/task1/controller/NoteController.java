package by.distcomp.task1.controller;

import by.distcomp.task1.dto.NoteRequestTo;
import by.distcomp.task1.dto.NoteResponseTo;
import by.distcomp.task1.dto.NoteResponseTo;
import by.distcomp.task1.service.NoteService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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
    public List<NoteResponseTo> getAllNotes(){
        return  noteService.getAllNotes();
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