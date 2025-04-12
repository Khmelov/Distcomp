package com.bsuir.romanmuhtasarov.controllers;

import com.bsuir.romanmuhtasarov.domain.request.LabelRequestTo;
import com.bsuir.romanmuhtasarov.domain.response.LabelResponseTo;
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
import com.bsuir.romanmuhtasarov.serivces.LabelService;

import java.util.List;

@RestController
@RequestMapping("/labels")
public class LabelController {
    private final LabelService labelService;

    @Autowired
    public LabelController(LabelService labelService) {
        this.labelService = labelService;
    }

    @PostMapping
    public ResponseEntity<LabelResponseTo> createMessage(@RequestBody LabelRequestTo labelRequestTo) {
        return ResponseEntity.status(HttpStatus.CREATED).body(labelService.create(labelRequestTo));
    }

    @GetMapping("/{id}")
    public ResponseEntity<LabelResponseTo> findMessageById(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(labelService.findLabelById(id));
    }

    @GetMapping
    public ResponseEntity<List<LabelResponseTo>> findAllMessages() {
        return ResponseEntity.status(HttpStatus.OK).body(labelService.read());
    }

    @PutMapping
    public ResponseEntity<LabelResponseTo> updateMessage(@RequestBody LabelRequestTo labelRequestTo) {
        return ResponseEntity.status(HttpStatus.OK).body(labelService.update(labelRequestTo));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Long> deleteMessageById(@PathVariable Long id) {
        labelService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(id);
    }
}
