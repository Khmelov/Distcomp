package com.example.demo.controller;


import com.example.demo.dto.CreatorResponseTo;
import com.example.demo.dto.NoteRequestTo;
import com.example.demo.dto.NoteResponseTo;
import com.example.demo.model.Note;
import com.example.demo.service.NoteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1.0/notes")
public class NoteController {
    private final NoteService service;
    @PostMapping
    public ResponseEntity<NoteResponseTo> createNote(@Valid @RequestBody NoteRequestTo request){
        NoteResponseTo response = service.createNote(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<NoteResponseTo> getNote(@PathVariable Long id){
        NoteResponseTo response = service.getNodeById(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<NoteResponseTo>> getAllCreators(){
        List<NoteResponseTo> responses = service.getAllNotes();
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }
    @PutMapping
    public ResponseEntity<NoteResponseTo> updateNote(@Valid @RequestBody NoteRequestTo request){
        NoteResponseTo response = service.updateNote(request);
        return response != null
                ? new ResponseEntity<>(response, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNoteById(@PathVariable Long id){

        try{
            service.deleteNote(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }catch (Exception e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
