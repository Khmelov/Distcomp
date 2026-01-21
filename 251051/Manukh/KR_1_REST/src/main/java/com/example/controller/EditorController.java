// EditorController.java
package com.example.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.dto.request.EditorRequestTo;
import com.example.dto.response.EditorResponseTo;
import com.example.service.EditorService;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0")
public class EditorController {

    @Autowired
    private EditorService editorService;

    @GetMapping("/editors")
    public ResponseEntity<List<EditorResponseTo>> getAllEditors() {
        return ResponseEntity.ok(editorService.getAllEditors());
    }

    @GetMapping("/editors/{id}")
    public ResponseEntity<EditorResponseTo> getEditorById(@PathVariable Long id) {
        return ResponseEntity.ok(editorService.getEditorById(id));
    }

    @PostMapping("/editors")
    public ResponseEntity<EditorResponseTo> createEditor(@Valid @RequestBody EditorRequestTo request) {
        EditorResponseTo response = editorService.createEditor(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/editors/{id}")
    public ResponseEntity<EditorResponseTo> updateEditor(
            @PathVariable Long id,
            @Valid @RequestBody EditorRequestTo request) {
        EditorResponseTo response = editorService.updateEditor(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/editors/{id}")
    public ResponseEntity<Void> deleteEditor(@PathVariable Long id) {
        editorService.deleteEditor(id);
        return ResponseEntity.noContent().build();
    }
}