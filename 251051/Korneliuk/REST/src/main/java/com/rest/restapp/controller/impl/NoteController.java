package com.rest.restapp.controller.impl;

import com.rest.restapp.controller.NoticeControllerApi;
import com.rest.restapp.dto.request.NoteRequestToDto;
import com.rest.restapp.dto.response.NoteResponseTo;
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
public class NoteController implements NoticeControllerApi {

    NoteService noteService;

    @PostMapping("/notes")
    public ResponseEntity<NoteResponseTo> createNote(NoteRequestToDto requestTo) {
        return ResponseEntity.status(HttpStatus.CREATED).body(noteService.createNote(requestTo));
    }

    @Override
    public ResponseEntity<NoteResponseTo> getNoteById(Long id) {
        return ResponseEntity.ok(noteService.getNoteById(id));
    }

    @Override
    public ResponseEntity<List<NoteResponseTo>> getAllNotes() {
        return ResponseEntity.ok(noteService.getAllNotes());
    }

    @Override
    public ResponseEntity<NoteResponseTo> updateNote(Long id, NoteRequestToDto requestTo) {
        return ResponseEntity.ok(noteService.updateNote(id, requestTo));
    }

    @Override
    public ResponseEntity<Void> deleteNote(Long id) {
        noteService.deleteNote(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<List<NoteResponseTo>> getNotesByIssueId(Long issueId) {
        return ResponseEntity.ok(noteService.getNotesByIssueId(issueId));
    }
}