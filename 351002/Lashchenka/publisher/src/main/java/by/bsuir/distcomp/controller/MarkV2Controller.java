package by.bsuir.distcomp.controller;

import by.bsuir.distcomp.dto.request.MarkRequestTo;
import by.bsuir.distcomp.dto.response.MarkResponseTo;
import by.bsuir.distcomp.security.V2Security;
import by.bsuir.distcomp.service.MarkService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v2.0/marks")
public class MarkV2Controller {

    private final MarkService markService;

    public MarkV2Controller(MarkService markService) {
        this.markService = markService;
    }

    @PostMapping
    public ResponseEntity<MarkResponseTo> create(@Valid @RequestBody MarkRequestTo dto) {
        V2Security.requireAdmin();
        MarkResponseTo response = markService.create(dto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MarkResponseTo> getById(@PathVariable Long id) {
        return ResponseEntity.ok(markService.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<MarkResponseTo>> getAll() {
        return ResponseEntity.ok(markService.getAll());
    }

    @PutMapping
    public ResponseEntity<MarkResponseTo> update(@Valid @RequestBody MarkRequestTo dto) {
        V2Security.requireAdmin();
        return ResponseEntity.ok(markService.update(dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        V2Security.requireAdmin();
        markService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
