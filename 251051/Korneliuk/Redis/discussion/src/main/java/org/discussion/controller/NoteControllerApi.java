package org.discussion.controller;

import com.common.NoteResponseTo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.discussion.dto.request.NoteRequestTo;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Tag(name = "Notes", description = "CRUD operations for Notes")
public interface NoteControllerApi {

    @Operation(summary = "Create Note")
    @ApiResponse(responseCode = "201", description = "Note создан")
    @PostMapping("/notes")
    ResponseEntity<NoteResponseTo> createNote(@Valid @RequestBody NoteRequestTo requestTo);

    @Operation(summary = "Get Note by id")
    @ApiResponse(responseCode = "200", description = "Note найден")
    @GetMapping("/notes/{id}")
    ResponseEntity<NoteResponseTo> getNoteById(@PathVariable(name = "id") Long id);

    @Operation(summary = "Get all Notes")
    @ApiResponse(responseCode = "200", description = "Все notes")
    @GetMapping("/notes")
    ResponseEntity<List<NoteResponseTo>> getAllNotes();

    @Operation(summary = "Update Note by id")
    @ApiResponse(responseCode = "200", description = "Note обновлен")
    @PutMapping("/notes/{id}")
    ResponseEntity<NoteResponseTo> updateNote(@PathVariable(name = "id") Long id,
                                              @Valid @RequestBody NoteRequestTo requestTo);

    @Operation(summary = "Delete Note by id")
    @ApiResponse(responseCode = "204", description = "Note удалён")
    @DeleteMapping("/notes/{id}")
    ResponseEntity<Void> deleteNote(@PathVariable(name = "id") Long id);

    @Operation(summary = "Get Notes by Issue id")
    @ApiResponse(responseCode = "200", description = "Notes по issueId")
    @GetMapping("/notes/issue/{issueId}")
    ResponseEntity<List<NoteResponseTo>> getNotesByIssueId(@PathVariable Long issueId);
}