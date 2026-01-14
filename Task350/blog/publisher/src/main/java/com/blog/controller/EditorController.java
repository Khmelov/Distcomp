package com.blog.controller;

import com.blog.dto.request.EditorRequestTo;
import com.blog.dto.response.EditorResponseTo;
import com.blog.service.EditorService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/editors")
public class EditorController {

    @Autowired
    private EditorService editorService;

    @GetMapping
    public ResponseEntity<List<EditorResponseTo>> getAllEditors() {
        // ВРЕМЕННОЕ РЕШЕНИЕ: возвращаем просто список без пагинации
        List<EditorResponseTo> editors = editorService.getAll();
        return ResponseEntity.ok(editors);
    }


    @GetMapping("/{id}")
    public ResponseEntity<EditorResponseTo> getEditorById(@PathVariable Long id) {
        EditorResponseTo editor = editorService.getById(id);
        return ResponseEntity.ok(editor);
    }

    @PostMapping
    public ResponseEntity<EditorResponseTo> createEditor(@Valid @RequestBody EditorRequestTo request) {
        EditorResponseTo createdEditor = editorService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdEditor);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EditorResponseTo> updateEditor(@PathVariable Long id,
                                                         @Valid @RequestBody EditorRequestTo request) {
        EditorResponseTo updatedEditor = editorService.update(id, request);
        return ResponseEntity.ok(updatedEditor);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEditor(@PathVariable Long id) {
        editorService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/test")
    public ResponseEntity<EditorResponseTo> getTestEditor() {
        EditorResponseTo testEditor = new EditorResponseTo(
                1L,
                "test@example.com",
                "TestFirstName",
                "TestLastName"
        );
        return ResponseEntity.ok(testEditor);
    }
}

