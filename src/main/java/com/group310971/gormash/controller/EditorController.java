package com.group310971.gormash.controller;

import com.group310971.gormash.dto.EditorRequestTo;
import com.group310971.gormash.dto.EditorResponseTo;
import com.group310971.gormash.service.EditorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1.0/editors")
public class EditorController {
    private final EditorService editorService;

    @PostMapping
    public ResponseEntity<EditorResponseTo> createEditor(@Valid @RequestBody EditorRequestTo editorRequestTo) {
        try {
            EditorResponseTo createdEditor = editorService.createEditor(editorRequestTo);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdEditor);
        } catch(Exception e){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new EditorResponseTo());
        }
    }

    @PutMapping
    public ResponseEntity<EditorResponseTo> updateEditor(@Valid @RequestBody EditorRequestTo editorRequestTo) {
        EditorResponseTo updatedEditor = editorService.updateEditor(editorRequestTo);
        return ResponseEntity.ok(updatedEditor);
    }

    @GetMapping
    public ResponseEntity<List<EditorResponseTo>> getAllEditors() {
        List<EditorResponseTo> editors = editorService.getAllEditors();
        return ResponseEntity.ok(editors);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EditorResponseTo> getEditorById(@PathVariable Long id) {
        EditorResponseTo editor = editorService.getEditorById(id);
        return ResponseEntity.ok(editor);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<EditorResponseTo> deleteEditor(@PathVariable Long id) {
        try {
            EditorResponseTo deleted = editorService.deleteEditor(id);
            return new ResponseEntity<>(deleted, HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
