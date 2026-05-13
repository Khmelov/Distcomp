package com.example.task361.controller;

import com.example.task361.domain.dto.request.MarkerRequestTo;
import com.example.task361.domain.dto.response.MarkerResponseTo;
import com.example.task361.service.MarkerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/markers")
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

    @PutMapping("/{id}")
    public MarkerResponseTo update(@PathVariable Long id, @Valid @RequestBody MarkerRequestTo request) {
        request.setId(id);
        return markerService.update(request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long id) {
        markerService.deleteById(id);
    }
}
