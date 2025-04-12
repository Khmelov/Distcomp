package com.bsuir.romanmuhtasarov.controllers;

import com.bsuir.romanmuhtasarov.domain.request.CreatorRequestTo;
import com.bsuir.romanmuhtasarov.domain.response.CreatorResponseTo;
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
import com.bsuir.romanmuhtasarov.serivces.CreatorService;

import java.util.List;

@RestController
@RequestMapping("/creators")
public class CreatorController {
    private final CreatorService creatorService;

    @Autowired
    public CreatorController(CreatorService creatorService) {
        this.creatorService = creatorService;
    }

    @PostMapping
    public ResponseEntity<CreatorResponseTo> createCreator(@RequestBody CreatorRequestTo creatorRequestTo) {
        return ResponseEntity.status(HttpStatus.CREATED).body(creatorService.create(creatorRequestTo));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CreatorResponseTo> findCreatorById(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(creatorService.findCreatorById(id));
    }

    @GetMapping
    public ResponseEntity<List<CreatorResponseTo>> findAllCreators() {
        return ResponseEntity.status(HttpStatus.OK).body(creatorService.read());
    }

    @PutMapping
    public ResponseEntity<CreatorResponseTo> updateCreator(@RequestBody CreatorRequestTo creatorRequestTo) {
        return ResponseEntity.status(HttpStatus.OK).body(creatorService.update(creatorRequestTo));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Long> deleteCreatorById(@PathVariable Long id) {
        creatorService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(id);
    }
}
