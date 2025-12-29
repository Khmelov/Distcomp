package com.rest.controller;

import com.rest.dto.request.WriterRequestTo;
import com.rest.dto.response.WriterResponseTo;
import com.rest.service.WriterService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1.0/writers")
public class WriterController {
    
	@Autowired
    private final WriterService writerService;
    
    public WriterController(WriterService writerService) {
        this.writerService = writerService;
    }
    
    @PostMapping
    public ResponseEntity<WriterResponseTo> create(@Valid @RequestBody WriterRequestTo request) {
        WriterResponseTo response = writerService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<WriterResponseTo> findById(@PathVariable Long id) {
        WriterResponseTo response = writerService.findById(id);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping
    public ResponseEntity<List<WriterResponseTo>> findAll() {
        List<WriterResponseTo> responses = writerService.findAll();
        return ResponseEntity.ok(responses);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<WriterResponseTo> update(@PathVariable Long id, 
                                                   @Valid @RequestBody WriterRequestTo request) {
        WriterResponseTo response = writerService.update(id, request);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        writerService.delete(id);
        return ResponseEntity.noContent().build();
    }
}