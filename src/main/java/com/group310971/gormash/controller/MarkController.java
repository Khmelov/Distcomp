package com.group310971.gormash.controller;

import com.group310971.gormash.dto.MarkRequestTo;
import com.group310971.gormash.dto.MarkResponseTo;
import com.group310971.gormash.service.MarkService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1.0/marks")
class MarkController {
    private final MarkService markService;

    @PostMapping
    public ResponseEntity<MarkResponseTo> createMark(@Valid @RequestBody MarkRequestTo markRequestTo) {
        try {
            MarkResponseTo createdMark = markService.createMark(markRequestTo);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdMark);
        } catch(Exception e){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new MarkResponseTo());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<MarkResponseTo> updateMark(@PathVariable Long id, @Valid @RequestBody MarkRequestTo markRequestTo) {
        MarkResponseTo updatedMark = markService.updateMark(id, markRequestTo);
        return ResponseEntity.ok(updatedMark);
    }

    @GetMapping
    public ResponseEntity<List<MarkResponseTo>> getAllMarks() {
        List<MarkResponseTo> marks = markService.getAllMarks();
        return ResponseEntity.ok(marks);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MarkResponseTo> getMarkById(@PathVariable Long id) {
        MarkResponseTo mark = markService.getMarkById(id);
        return ResponseEntity.ok(mark);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MarkResponseTo> deleteMark(@PathVariable Long id) {
        try {
            MarkResponseTo deleted = markService.deleteMark(id);
            return new ResponseEntity<>(deleted, HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
