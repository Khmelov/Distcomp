package com.example.rv.api.Controllers;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.example.rv.impl.note.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping(value = "/api/v1.0")
public class NoteController {

    private final NoteService noteService;

    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }
    
    @RequestMapping(value = "/notes", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    List<NoteResponseTo> getNotes() {
        return noteService.noteMapper.noteToResponseTo(noteService.noteCrudRepository.getAll());
    }

    @RequestMapping(value = "/notes", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    NoteResponseTo makeNote(@RequestBody NoteRequestTo noteRequestTo) {

        var toBack = noteService.noteCrudRepository.save(
                noteService.noteMapper.dtoToEntity(noteRequestTo)
        );

        Note note = toBack.orElse(null);

        assert note != null;
        return noteService.noteMapper.noteToResponseTo(note);
    }

    @RequestMapping(value = "/notes/{id}", method = RequestMethod.GET)
    NoteResponseTo getNote(@PathVariable Long id) {
        return noteService.noteMapper.noteToResponseTo(
                Objects.requireNonNull(noteService.noteCrudRepository.getById(id).orElse(null)));
    }

    @RequestMapping(value = "/notes", method = RequestMethod.PUT)
    NoteResponseTo updateNote(@RequestBody NoteRequestTo noteRequestTo, HttpServletResponse response) {
        Note note = noteService.noteMapper.dtoToEntity(noteRequestTo);
        var newNote = noteService.noteCrudRepository.update(note).orElse(null);
        if (newNote != null) {
            response.setStatus(200);
            return noteService.noteMapper.noteToResponseTo(newNote);
        } else {
            response.setStatus(403);
            return noteService.noteMapper.noteToResponseTo(note);
        }
    }

    @RequestMapping(value = "/notes/{id}", method = RequestMethod.DELETE)
    int deleteNote(@PathVariable Long id, HttpServletResponse response) {
        Note noteToDelete = noteService.noteCrudRepository.getById(id).orElse(null);
        if (Objects.isNull(noteToDelete)) {
            response.setStatus(403);
        } else {
            noteService.noteCrudRepository.delete(noteToDelete);
            response.setStatus(204);
        }
        return 0;
    }
}
