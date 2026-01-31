package org.polozkov.controller.label;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.polozkov.dto.label.LabelRequestTo;
import org.polozkov.dto.label.LabelResponseTo;
import org.polozkov.service.label.LabelService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/labels")
@RequiredArgsConstructor
public class LabelController {

    private final LabelService labelService;

    @GetMapping
    public List<LabelResponseTo> getAllLabels() {
        return labelService.getAllLabels();
    }

    @GetMapping("/{id}")
    public LabelResponseTo getLabel(@PathVariable Long id) {
        return labelService.getLabel(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LabelResponseTo createLabel(@Valid @RequestBody LabelRequestTo labelRequest) {
        return labelService.createLabel(labelRequest);
    }

    @PutMapping
    public LabelResponseTo updateLabel(@Valid @RequestBody LabelRequestTo labelRequest) {
        return labelService.updateLabel(labelRequest);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteLabel(@PathVariable Long id) {
        labelService.deleteLabel(id);
    }
}