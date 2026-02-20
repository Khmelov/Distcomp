package com.example.demo.controllers;

import com.example.demo.dto.requests.MarkRequestTo;
import com.example.demo.dto.responses.MarkResponseTo;
import com.example.demo.service.MarkService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/marks")
@Validated
public class MarkController {
    private final MarkService markService;

    public MarkController(MarkService markService) {
        this.markService = markService;
    }

    @PostMapping
    public ResponseEntity<MarkResponseTo> create(@RequestBody MarkRequestTo dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(markService.create(dto));
    }

    @GetMapping
    public ResponseEntity<List<MarkResponseTo>> findAll(){
        List<MarkResponseTo> list = markService.findAll();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MarkResponseTo> findById(@PathVariable Long id){
        MarkResponseTo mark = markService.findById(id);
        return ResponseEntity.ok(mark);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MarkResponseTo> update(@PathVariable Long id,
                                                  @RequestBody MarkRequestTo dto){
        MarkResponseTo updated = markService.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        markService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
