package com.example.task340.controller;

import com.example.task340.domain.dto.request.MarkerRequestTo;
import com.example.task340.domain.dto.response.MarkerResponseTo;
import com.example.task340.domain.dto.response.TweetResponseTo;
import com.example.task340.service.MarkerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/markers")
@RequiredArgsConstructor
public class MarkerController {

    private final MarkerService markerService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MarkerResponseTo create(@Valid @RequestBody MarkerRequestTo request) {
        return markerService.create(request);
    }

    @GetMapping
    public List<MarkerResponseTo> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return markerService.findAll(page, size);
    }

    @GetMapping("/{id}")
    public MarkerResponseTo findById(@PathVariable Long id) {
        return markerService.findById(id);
    }

    @PutMapping
    public MarkerResponseTo update(@Valid @RequestBody MarkerRequestTo request) {
        return markerService.update(request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long id) {
        markerService.deleteById(id);
    }
}