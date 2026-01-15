package com.example.task320.controller;

import com.example.task320.dto.request.NoticeRequestTo;
import com.example.task320.dto.response.NoticeResponseTo;
import com.example.task320.service.NoticeService;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/notices")
public class NoticeController {

    private final NoticeService service;

    public NoticeController(NoticeService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<NoticeResponseTo> create(@Valid @RequestBody NoticeRequestTo body) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(body));
    }

    @GetMapping
    public List<NoticeResponseTo> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public NoticeResponseTo getById(@PathVariable long id) {
        return service.getById(id);
    }

    @PutMapping("/{id}")
    public NoticeResponseTo update(@PathVariable long id, @Valid @RequestBody NoticeRequestTo body) {
        return service.update(id, body);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable long id) {
        service.delete(id);
    }
}
