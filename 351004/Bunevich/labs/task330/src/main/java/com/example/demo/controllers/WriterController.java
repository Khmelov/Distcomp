package com.example.demo.controllers;

import com.example.demo.dto.request.WriterRequestTo;
import com.example.demo.dto.response.WriterResponseTo;
import com.example.demo.servises.WriterServise;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/writers")
public class WriterController {

    private static final Logger log = LoggerFactory.getLogger(WriterController.class);

    private final WriterServise writerService;

    public WriterController(WriterServise writerService) {
        this.writerService = writerService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public WriterResponseTo createWriter(@Valid @RequestBody WriterRequestTo writerRequest) {
        log.info("REST request to create writer: {}", writerRequest);
        return writerService.create(writerRequest);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<WriterResponseTo> getAllWriters(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String login) {
        return writerService.findAll(page, size, sortBy, sortDir, login);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteWriter(@PathVariable Long id) {
        writerService.delete(id);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public WriterResponseTo getWriterByid(@PathVariable Long id) {
        return writerService.findById(id);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public WriterResponseTo updateWriter(@PathVariable Long id, @Valid @RequestBody WriterRequestTo writerRequestTo) {
        return writerService.update(id, writerRequestTo);
    }
}