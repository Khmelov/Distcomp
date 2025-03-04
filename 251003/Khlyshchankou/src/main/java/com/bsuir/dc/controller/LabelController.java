package com.bsuir.dc.controller;

import com.bsuir.dc.dto.request.LabelRequestTo;
import com.bsuir.dc.dto.response.LabelResponseTo;
import com.bsuir.dc.service.LabelService;
import com.bsuir.dc.util.exception.ValidationException;
import com.bsuir.dc.util.validator.LabelValidator;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/labels")
public class LabelController {
    private final LabelService labelService;
    private final LabelValidator labelValidator;

    @Autowired
    public LabelController(LabelService labelService, LabelValidator labelValidator) {
        this.labelService = labelService;
        this.labelValidator = labelValidator;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LabelResponseTo createLabel(@RequestBody @Valid LabelRequestTo labelRequestTo, BindingResult bindingResult) {
        validate(labelRequestTo, bindingResult);
        return labelService.save(labelRequestTo);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<LabelResponseTo> getAllLabels() { return labelService.findAll(); }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public LabelResponseTo getLabelById(@PathVariable Long id) { return labelService.findById(id); }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public LabelResponseTo updateLabel(@RequestBody @Valid LabelRequestTo labelRequestTo, BindingResult bindingResult){
        validate(labelRequestTo, bindingResult);
        return labelService.update(labelRequestTo);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteLabel(@PathVariable long id) { labelService.deleteById(id); }

    private void validate(LabelRequestTo labelRequestTo, BindingResult bindingResult){
        labelValidator.validate(labelRequestTo, bindingResult);
        if (bindingResult.hasFieldErrors()) { throw new ValidationException(bindingResult); }
    }
}
