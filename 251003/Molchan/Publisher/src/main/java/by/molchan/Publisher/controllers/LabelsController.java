package by.molchan.Publisher.controllers;


import by.molchan.Publisher.DTOs.Requests.LabelRequestDTO;
import by.molchan.Publisher.DTOs.Responses.LabelResponseDTO;
import by.molchan.Publisher.services.LabelsService;
import by.molchan.Publisher.utils.LabelValidator;
import by.molchan.Publisher.utils.exceptions.ValidationException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/labels")
public class LabelsController {
    private final LabelsService labelsService;

    private final LabelValidator labelValidator;

    @Autowired
    public LabelsController(LabelsService labelsService, LabelValidator labelValidator) {
        this.labelsService = labelsService;
        this.labelValidator = labelValidator;
    }


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LabelResponseDTO createLabel(@RequestBody @Valid LabelRequestDTO labelRequestDTO, BindingResult bindingResult) {
        validate(labelRequestDTO, bindingResult);
        return labelsService.save(labelRequestDTO);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<LabelResponseDTO> getAllLabels() {
        return labelsService.findAll();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public LabelResponseDTO getLabelById(@PathVariable Long id) {
        return labelsService.findById(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteLabel(@PathVariable long id){
        labelsService.deleteById(id);
    }

    // Non REST version for test compliance
    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public LabelResponseDTO updateLabel(@RequestBody @Valid LabelRequestDTO labelRequestDTO, BindingResult bindingResult){
        validate(labelRequestDTO, bindingResult);
        return labelsService.update(labelRequestDTO);
    }

    private void validate(LabelRequestDTO labelRequestDTO, BindingResult bindingResult){
        labelValidator.validate(labelRequestDTO, bindingResult);
        if (bindingResult.hasFieldErrors()){
            throw new ValidationException(bindingResult);
        }
    }
}
