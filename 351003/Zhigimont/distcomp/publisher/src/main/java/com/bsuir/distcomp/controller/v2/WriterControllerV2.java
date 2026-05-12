package com.bsuir.distcomp.controller.v2;

import com.bsuir.distcomp.dto.WriterRequestTo;
import com.bsuir.distcomp.dto.WriterResponseTo;
import com.bsuir.distcomp.service.WriterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v2.0/writers")
@RequiredArgsConstructor
public class WriterControllerV2 {

    private final WriterService writerService;

    @GetMapping
    public ResponseEntity<List<WriterResponseTo>> getAll() {
        return ResponseEntity.ok(writerService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<WriterResponseTo> getById(@PathVariable Long id) {
        return ResponseEntity.ok(writerService.getById(id));
    }

    @PostMapping
    public ResponseEntity<WriterResponseTo> create(@RequestBody WriterRequestTo request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(writerService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<WriterResponseTo> update(@PathVariable Long id, @RequestBody WriterRequestTo request) {
        return ResponseEntity.ok(writerService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        writerService.delete(id);
        return ResponseEntity.noContent().build();
    }
}