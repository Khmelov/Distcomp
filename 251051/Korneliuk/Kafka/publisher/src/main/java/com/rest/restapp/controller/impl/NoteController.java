package com.rest.restapp.controller.impl;

import com.common.NoteResponseTo;
import com.rest.restapp.controller.NoteControllerApi;
import com.rest.restapp.dto.request.NoteRequestTo;
import com.rest.restapp.service.NoteService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class NoteController implements NoteControllerApi {

    NoteService noteService;

    @PostMapping("/notes")
    public ResponseEntity<NoteResponseTo> createNote(NoteRequestTo requestTo) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(noteService.createNote(requestTo));
    }

    @Override
    public ResponseEntity<NoteResponseTo> getNoteById(Long id) {
        var response = noteService.getNoteById(id);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<List<NoteResponseTo>> getAllNotes() {
        var responses = noteService.getAllNotes();
        return ResponseEntity.ok(responses);
    }

    @Override
    public ResponseEntity<NoteResponseTo> updateNote(NoteRequestTo requestTo) {
        var response = noteService.updateNote(requestTo);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Void> deleteNote(Long id) {
        noteService.deleteNote(id);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<List<NoteResponseTo>> getNotesByIssueId(Long issueId) {
        var responses = noteService.getNotesByIssueId(issueId);
        return ResponseEntity.ok(responses);
    }
}