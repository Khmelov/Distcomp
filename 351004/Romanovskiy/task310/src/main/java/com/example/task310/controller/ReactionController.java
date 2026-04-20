package com.example.task310.controller;

import com.example.task310.domain.dto.request.ReactionRequestTo;
import com.example.task310.domain.dto.response.ReactionResponseTo;
import com.example.task310.service.ReactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reactions")
@RequiredArgsConstructor
public class ReactionController {

    private final ReactionService reactionService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReactionResponseTo create(@Valid @RequestBody ReactionRequestTo request) {
        return reactionService.create(request);
    }

    @GetMapping
    public List<ReactionResponseTo> findAll() {
        return reactionService.findAll();
    }

    @GetMapping("/{id}")
    public ReactionResponseTo findById(@PathVariable Long id) {
        return reactionService.findById(id);
    }

    @PutMapping
    public ReactionResponseTo update(@Valid @RequestBody ReactionRequestTo request) {
        return reactionService.update(request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long id) {
        reactionService.deleteById(id);
    }
}