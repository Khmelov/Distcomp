package by.bsuir.distcomp.controller;

import by.bsuir.distcomp.dto.request.EditorRequestTo;
import by.bsuir.distcomp.dto.response.EditorResponseTo;
import by.bsuir.distcomp.service.EditorService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/editors")
public class EditorController {

    private final EditorService editorService;

    public EditorController(EditorService editorService) {
        this.editorService = editorService;
    }

    @PostMapping
    public ResponseEntity<EditorResponseTo> create(@Valid @RequestBody EditorRequestTo dto) {
        EditorResponseTo response = editorService.create(dto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EditorResponseTo> getById(@PathVariable Long id) {
        EditorResponseTo response = editorService.getById(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<EditorResponseTo>> getAll() {
        List<EditorResponseTo> response = editorService.getAll();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<EditorResponseTo> update(@Valid @RequestBody EditorRequestTo dto) {
        EditorResponseTo response = editorService.update(dto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        editorService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
