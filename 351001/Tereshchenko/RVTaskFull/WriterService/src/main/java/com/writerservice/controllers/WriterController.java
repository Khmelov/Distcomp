package com.writerservice.controllers;

import com.writerservice.dtos.WriterRequestTo;
import com.writerservice.dtos.WriterResponseTo;
import com.writerservice.sevices.WriterService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1.0/")
@RequiredArgsConstructor
public class WriterController {

    private final WriterService writerService;

    @PostMapping("/writers")
    public ResponseEntity<WriterResponseTo> createWriter(@Valid @RequestBody WriterRequestTo request) {
        return new ResponseEntity<>(writerService.createWriter(request), HttpStatus.CREATED);
    }

    @GetMapping("/writers")
    public ResponseEntity<List<WriterResponseTo>> getAllWriters() {
        return ResponseEntity.ok(writerService.findAllWriters());
    }

    @GetMapping("/writers/{id}")
    public ResponseEntity<WriterResponseTo> getWriterById(@PathVariable Long id) {
        return ResponseEntity.ok(writerService.findWriterById(id));
    }

    @PutMapping("/writers/{id}")
    public ResponseEntity<WriterResponseTo> updateWriteById(@Valid @RequestBody WriterRequestTo request, @PathVariable Long id) {
        return ResponseEntity.ok(writerService.updateProfile(request, id));
    }

    @DeleteMapping("/writers/{id}")
    public ResponseEntity<Void> deleteWriterById(@PathVariable Long id) {
        writerService.deleteWriter(id);
        return ResponseEntity.noContent().build();
    }


}
