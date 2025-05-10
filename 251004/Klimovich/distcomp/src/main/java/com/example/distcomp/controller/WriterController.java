package com.example.distcomp.controller;

import com.example.distcomp.dto.WriterRequestTo;
import com.example.distcomp.dto.WriterResponseTo;
import com.example.distcomp.mapper.WriterMapper;
import com.example.distcomp.model.Writer;
import com.example.distcomp.service.WriterService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/writers")
@RequiredArgsConstructor
public class WriterController {
    private final WriterService writerService;

    @PostMapping
    public ResponseEntity<WriterResponseTo> createWriter(@RequestBody WriterRequestTo request) {
        WriterResponseTo response = writerService.createWriter(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<WriterResponseTo>> getAllWriters() {
        return ResponseEntity.ok(writerService.getAllWriters());
    }

    @GetMapping("/{id}")
    public ResponseEntity<WriterResponseTo> getWriterById(@PathVariable Long id) {
        return ResponseEntity.ok(writerService.getWriterById(id));
    }

    @PutMapping
    public ResponseEntity<WriterResponseTo> updateWriter(@RequestBody WriterRequestTo request) {
        return ResponseEntity.ok(writerService.updateWriter(request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWriter(@PathVariable Long id) {
        writerService.deleteWriter(id);
        return ResponseEntity.noContent().build();
    }
}