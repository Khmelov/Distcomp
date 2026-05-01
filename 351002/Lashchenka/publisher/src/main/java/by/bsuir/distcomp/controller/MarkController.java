package by.bsuir.distcomp.controller;

import by.bsuir.distcomp.dto.request.MarkRequestTo;
import by.bsuir.distcomp.dto.response.MarkResponseTo;
import by.bsuir.distcomp.service.MarkService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/marks")
public class MarkController {

    private final MarkService markService;

    public MarkController(MarkService markService) {
        this.markService = markService;
    }

    @PostMapping
    public ResponseEntity<MarkResponseTo> create(@Valid @RequestBody MarkRequestTo dto) {
        MarkResponseTo response = markService.create(dto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MarkResponseTo> getById(@PathVariable Long id) {
        MarkResponseTo response = markService.getById(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<MarkResponseTo>> getAll() {
        List<MarkResponseTo> response = markService.getAll();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<MarkResponseTo> update(@Valid @RequestBody MarkRequestTo dto) {
        MarkResponseTo response = markService.update(dto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        markService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
