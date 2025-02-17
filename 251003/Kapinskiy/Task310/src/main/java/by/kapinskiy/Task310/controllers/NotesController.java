package by.kapinskiy.Task310.controllers;


import by.kapinskiy.Task310.DTOs.Requests.NoteRequestDTO;
import by.kapinskiy.Task310.DTOs.Responses.NoteResponseDTO;
import by.kapinskiy.Task310.models.Note;
import by.kapinskiy.Task310.services.NotesService;
import by.kapinskiy.Task310.utils.Mapper;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notes")
public class NotesController {
    private final Mapper mapper;
    private final NotesService notesService;

    @Autowired
    public NotesController(Mapper mapper, NotesService notesService) {
        this.mapper = mapper;
        this.notesService = notesService;
    }


    @PostMapping
    public ResponseEntity<NoteResponseDTO> createNote(@RequestBody @Valid NoteRequestDTO noteRequestDTO) {
        Note note = mapper.noteRequestToNote(noteRequestDTO);
        return new ResponseEntity<>(mapper.noteToNoteResponse(notesService.save(note, noteRequestDTO.getIssueId())), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<NoteResponseDTO>> getAllNotes() {
        return new ResponseEntity<>(mapper.notesToNoteResponses(notesService.findAll()), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<NoteResponseDTO> getNoteById(@PathVariable Long id) {
        return new ResponseEntity<>(mapper.noteToNoteResponse(notesService.findById(id)), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNote(@PathVariable long id){
        notesService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // The same as in Users Put
    @PutMapping
    public ResponseEntity<NoteResponseDTO> updateNote(@RequestBody @Valid NoteRequestDTO noteRequestDTO){
        Note updatedNote = notesService.update(mapper.noteRequestToNote(noteRequestDTO), noteRequestDTO.getIssueId());
        return new ResponseEntity<>(mapper.noteToNoteResponse(updatedNote), HttpStatus.OK);
    }
}
