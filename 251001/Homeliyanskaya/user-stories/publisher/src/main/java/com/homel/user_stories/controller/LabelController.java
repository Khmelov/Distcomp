package com.homel.user_stories.controller;

import com.homel.user_stories.dto.LabelRequestTo;
import com.homel.user_stories.dto.LabelResponseTo;
import com.homel.user_stories.service.LabelService;
import jakarta.validation.Valid;
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
@RequestMapping("/api/v1.0/labels")
public class LabelController {

    private final LabelService labelService;

    public LabelController(LabelService labelService) {
        this.labelService = labelService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LabelResponseTo createLabel(@Valid @RequestBody LabelRequestTo labelRequest) {
        return labelService.createLabel(labelRequest);
    }

    @GetMapping("/{id}")
    public LabelResponseTo getLabel(@PathVariable Long id) {
        return labelService.getLabel(id);
    }

    @GetMapping
    public List<LabelResponseTo> getAllLabels() {
        return labelService.getAllLabels();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteLabel(@PathVariable Long id) {
        labelService.deleteLabel(id);
    }

    @PutMapping
    public LabelResponseTo updateLabel(@Valid @RequestBody LabelRequestTo labelRequest) {
        return labelService.updateLabel(labelRequest);
    }
}
