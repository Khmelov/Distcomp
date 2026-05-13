package com.sergey.orsik.controller;

import com.sergey.orsik.dto.request.LabelRequestTo;
import com.sergey.orsik.dto.response.LabelResponseTo;
import com.sergey.orsik.service.LabelService;
import com.sergey.orsik.service.SecuredResourceService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v2.0/labels")
public class LabelV2Controller {

    private final LabelService labelService;
    private final SecuredResourceService securedResourceService;

    public LabelV2Controller(LabelService labelService, SecuredResourceService securedResourceService) {
        this.labelService = labelService;
        this.securedResourceService = securedResourceService;
    }

    @GetMapping
    public List<LabelResponseTo> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String name) {
        return labelService.findAll(page, size, sortBy, sortDir, name);
    }

    @GetMapping("/{id}")
    public LabelResponseTo findById(@PathVariable Long id) {
        return labelService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LabelResponseTo create(@Valid @RequestBody LabelRequestTo request) {
        securedResourceService.requireAdmin();
        return labelService.create(request);
    }

    @PutMapping
    public LabelResponseTo updateByBody(@Valid @RequestBody LabelRequestTo request) {
        if (request.getId() == null) {
            throw new IllegalArgumentException("id is required for update");
        }
        securedResourceService.requireAdmin();
        return labelService.update(request.getId(), request);
    }

    @PutMapping("/{id}")
    public LabelResponseTo update(@PathVariable Long id, @Valid @RequestBody LabelRequestTo request) {
        securedResourceService.requireAdmin();
        return labelService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long id) {
        securedResourceService.requireAdmin();
        labelService.deleteById(id);
    }
}
