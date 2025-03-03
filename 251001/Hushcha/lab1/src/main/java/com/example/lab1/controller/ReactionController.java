package com.example.lab1.controller;

import com.example.lab1.dto.ReactionRequestTo;
import com.example.lab1.dto.ReactionResponseTo;
import com.example.lab1.dto.UserRequestTo;
import com.example.lab1.dto.UserResponseTo;
import com.example.lab1.service.ReactionService;
import com.example.lab1.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/notes")
@RequiredArgsConstructor
public class ReactionController {
    private final ReactionService reactionService;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public List<ReactionResponseTo> getReactions() {
        return reactionService.getAll();
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{id}")
    public ReactionResponseTo getReaction(@PathVariable long id) {
        return reactionService.getById(id);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ReactionResponseTo createReaction(@Valid @RequestBody ReactionRequestTo reactionRequestTo) {
        return reactionService.create(reactionRequestTo);
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping
    public ReactionResponseTo updateReaction(@Valid @RequestBody ReactionRequestTo reactionRequestTo) {
        return reactionService.update(reactionRequestTo);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void deleteReaction(@PathVariable Long id) {
        reactionService.deleteById(id);
    }
}

