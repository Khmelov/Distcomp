package com.example.publisher.controller;

import com.example.publisher.entity.Creator;
import com.example.publisher.repository.CreatorRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/v1.0/creators")
public class CreatorController {

    @Autowired
    private CreatorRepository creatorRepository;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Creator createCreator(@Valid @RequestBody Creator creator) {
        if (creatorRepository.findByLogin(creator.getLogin()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Login already exists");
        }
        return creatorRepository.save(creator);
    }

    @GetMapping
    public Iterable<Creator> getAllCreators() {
        return creatorRepository.findAll();
    }

    @GetMapping("/{id}")
    public Creator getCreator(@PathVariable String id) {
        try {
            Long longId = Long.parseLong(id);
            return creatorRepository.findById(longId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        } catch (NumberFormatException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid ID format");
        }
    }

    @PutMapping("/{id}")
    public Creator updateCreator(@PathVariable String id, @Valid @RequestBody Creator creator) {
        try {
            Long longId = Long.parseLong(id);
            if (!creatorRepository.existsById(longId)) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            }
            creator.setId(longId);
            return creatorRepository.save(creator);
        } catch (NumberFormatException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid ID format");
        }
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCreator(@PathVariable String id) {
        try {
            Long longId = Long.parseLong(id);
            creatorRepository.deleteById(longId);
        } catch (NumberFormatException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid ID format");
        }
    }
}