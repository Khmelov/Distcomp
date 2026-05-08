package com.example.task330.controller;

import com.example.task330.domain.dto.request.ReactionRequestTo;
import com.example.task330.domain.dto.response.ReactionResponseTo;
import com.example.task330.service.ReactionService;
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
    public ReactionResponseTo create(@RequestBody ReactionRequestTo request) {
        return reactionService.create(request);
    }

    @GetMapping
    public List<ReactionResponseTo> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return reactionService.findAll(page, size);
    }

    @GetMapping("/{id}")
    public ReactionResponseTo findById(@PathVariable Long id) {
        return reactionService.findById(id);
    }

    // Замени старый метод @PutMapping на этот:
    @PutMapping("/{id}")
    public ReactionResponseTo update(@PathVariable Long id, @RequestBody ReactionRequestTo request) {
        request.setId(id); // Убеждаемся, что ID из URL попал в объект
        return reactionService.update(request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long id) {
        reactionService.deleteById(id);
    }
}