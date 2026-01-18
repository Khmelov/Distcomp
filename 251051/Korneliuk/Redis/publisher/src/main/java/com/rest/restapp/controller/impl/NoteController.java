package com.rest.restapp.controller.impl;

import com.common.NoteResponseTo;
import com.rest.restapp.controller.NoteControllerApi;
import com.rest.restapp.dto.request.NoteRequestToDto;
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

    @Override
    public ResponseEntity<NoteResponseTo> createNote(NoteRequestToDto requestTo) {
        var response = noteService.createNotice(requestTo);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Override
    public ResponseEntity<NoteResponseTo> getNoteById(Long id) {
        var response = noteService.getNoticeById(id);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<List<NoteResponseTo>> getAllNotes() {
        var responses = noteService.getAllNotices();
        return ResponseEntity.ok(responses);
    }

    @Override
    public ResponseEntity<NoteResponseTo> updateNote(NoteRequestToDto requestTo) {
        var response = noteService.updateNotice(requestTo);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Void> deleteNote(Long id) {
        noteService.deleteNotice(id);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<List<NoteResponseTo>> getNotesByIssueId(Long issueId) {
        var responses = noteService.getNoticesByIssueId(issueId);
        return ResponseEntity.ok(responses);
    }
}