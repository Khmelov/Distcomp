package com.bsuir.romanmuhtasarov.controllers;

import com.bsuir.romanmuhtasarov.domain.request.WriterRequestTo;
import com.bsuir.romanmuhtasarov.domain.response.WriterResponseTo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.bsuir.romanmuhtasarov.serivces.WriterService;

import java.util.List;

@RestController
@RequestMapping("/writers")
public class WriterController {
    private final WriterService writerService;

    @Autowired
    public WriterController(WriterService writerService) {
        this.writerService = writerService;
    }

    @PostMapping
    public ResponseEntity<WriterResponseTo> createWriter(@RequestBody WriterRequestTo writerRequestTo) {
        return ResponseEntity.status(HttpStatus.CREATED).body(writerService.create(writerRequestTo));
    }

    @GetMapping("/{id}")
    public ResponseEntity<WriterResponseTo> findWriterById(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(writerService.findWriterById(id));
    }

    @GetMapping
    public ResponseEntity<List<WriterResponseTo>> findAllWriters() {
        return ResponseEntity.status(HttpStatus.OK).body(writerService.read());
    }

    @PutMapping
    public ResponseEntity<WriterResponseTo> updateWriter(@RequestBody WriterRequestTo writerRequestTo) {
        return ResponseEntity.status(HttpStatus.OK).body(writerService.update(writerRequestTo));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Long> deleteWriterById(@PathVariable Long id) {
        writerService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(id);
    }
}
