package com.sergey.orsik.controller;

import com.sergey.orsik.dto.request.LabelRequestTo;
import com.sergey.orsik.dto.response.LabelResponseTo;
import com.sergey.orsik.service.LabelService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/labels")
public class LabelController {

    private final LabelService labelService;

    public LabelController(LabelService labelService) {
        this.labelService = labelService;
    }

    @GetMapping
    public List<LabelResponseTo> findAll() {
        return labelService.findAll();
    }

    @GetMapping("/{id}")
    public LabelResponseTo findById(@PathVariable Long id) {
        return labelService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LabelResponseTo create(@Valid @RequestBody LabelRequestTo request) {
        return labelService.create(request);
    }

    @PutMapping
    public LabelResponseTo updateByBody(@Valid @RequestBody LabelRequestTo request) {
        if (request.getId() == null) {
            throw new IllegalArgumentException("id is required for update");
        }
        return labelService.update(request.getId(), request);
    }

    @PutMapping("/{id}")
    public LabelResponseTo update(@PathVariable Long id, @Valid @RequestBody LabelRequestTo request) {
        return labelService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long id) {
        labelService.deleteById(id);
    }
}
