package com.example.rv.api.Controllers;

import com.example.rv.impl.label.*;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping(value = "/api/v1.0")
public class LabelController {
    private final LabelService labelService;

    public LabelController(LabelService labelService) {
        this.labelService = labelService;
    }


    @RequestMapping(value = "/labels", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    List<LabelResponseTo> getLabels() {
        return labelService.labelMapper.labelToResponseTo(labelService.labelCrudRepository.getAll());
    }

    @RequestMapping(value = "/labels", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    LabelResponseTo makeLabel(@RequestBody LabelRequestTo labelRequestTo) {

        var toBack = labelService.labelCrudRepository.save(
                labelService.labelMapper.dtoToEntity(labelRequestTo)
        );

        Label label = toBack.orElse(null);

        assert label != null;
        return labelService.labelMapper.labelToResponseTo(label);
    }

    @RequestMapping(value = "/labels/{id}", method = RequestMethod.GET)
    LabelResponseTo getLabel(@PathVariable Long id) {
        return labelService.labelMapper.labelToResponseTo(
                Objects.requireNonNull(labelService.labelCrudRepository.getById(id).orElse(null)));
    }

    @RequestMapping(value = "/labels", method = RequestMethod.PUT)
    LabelResponseTo updateLabel(@RequestBody LabelRequestTo labelRequestTo, HttpServletResponse response) {
        Label label = labelService.labelMapper.dtoToEntity(labelRequestTo);
        var newlabel = labelService.labelCrudRepository.update(label).orElse(null);
        if (newlabel != null) {
            response.setStatus(200);
            return labelService.labelMapper.labelToResponseTo(newlabel);
        } else {
            response.setStatus(403);
            return labelService.labelMapper.labelToResponseTo(label);
        }
    }

    @RequestMapping(value = "/labels/{id}", method = RequestMethod.DELETE)
    int deleteLabel(@PathVariable Long id, HttpServletResponse response) {
        Label labelToDelete = labelService.labelCrudRepository.getById(id).orElse(null);
        if (Objects.isNull(labelToDelete)) {
            response.setStatus(403);
        } else {
            labelService.labelCrudRepository.delete(labelToDelete);
            response.setStatus(204);
        }
        return 0;
    }
}
