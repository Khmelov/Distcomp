package by.bsuir.distcomp.controller;

import by.bsuir.distcomp.dto.request.EditorRegistrationTo;
import by.bsuir.distcomp.dto.request.EditorRequestTo;
import by.bsuir.distcomp.dto.response.EditorResponseTo;
import by.bsuir.distcomp.security.V2Security;
import by.bsuir.distcomp.service.AuthService;
import by.bsuir.distcomp.service.EditorService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v2.0/editors")
public class EditorV2Controller {

    private final EditorService editorService;
    private final AuthService authService;

    public EditorV2Controller(EditorService editorService, AuthService authService) {
        this.editorService = editorService;
        this.authService = authService;
    }

    @PostMapping
    public ResponseEntity<EditorResponseTo> register(@Valid @RequestBody EditorRegistrationTo dto) {
        EditorResponseTo body = authService.register(dto);
        return new ResponseEntity<>(body, HttpStatus.CREATED);
    }

    @GetMapping("/me")
    public ResponseEntity<EditorResponseTo> me() {
        return ResponseEntity.ok(authService.currentEditor());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EditorResponseTo> getById(@PathVariable Long id) {
        return ResponseEntity.ok(editorService.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<EditorResponseTo>> getAll() {
        return ResponseEntity.ok(editorService.getAll());
    }

    @PutMapping
    public ResponseEntity<EditorResponseTo> update(@Valid @RequestBody EditorRequestTo dto) {
        V2Security.requireSelfOrAdmin(dto.getId());
        return ResponseEntity.ok(editorService.update(dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        V2Security.requireSelfOrAdmin(id);
        editorService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
