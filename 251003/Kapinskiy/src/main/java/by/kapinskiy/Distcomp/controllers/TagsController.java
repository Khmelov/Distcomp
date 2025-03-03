package by.kapinskiy.Distcomp.controllers;


import by.kapinskiy.Distcomp.DTOs.Requests.TagRequestDTO;
import by.kapinskiy.Distcomp.DTOs.Responses.TagResponseDTO;
import by.kapinskiy.Distcomp.models.Tag;
import by.kapinskiy.Distcomp.services.TagsService;
import by.kapinskiy.Distcomp.utils.TagValidator;
import by.kapinskiy.Distcomp.utils.exceptions.ValidationException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tags")
public class TagsController {
    private final TagsService tagsService;

    private final TagValidator tagValidator;

    @Autowired
    public TagsController(TagsService tagsService, TagValidator tagValidator) {
        this.tagsService = tagsService;
        this.tagValidator = tagValidator;
    }


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TagResponseDTO createTag(@RequestBody @Valid TagRequestDTO tagRequestDTO, BindingResult bindingResult) {
        validate(tagRequestDTO, bindingResult);
        return tagsService.save(tagRequestDTO);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<TagResponseDTO> getAllTags() {
        return tagsService.findAll();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TagResponseDTO getTagById(@PathVariable Long id) {
        return tagsService.findById(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTag(@PathVariable long id){
        tagsService.deleteById(id);
    }

    // Non REST version for test compliance
    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public TagResponseDTO updateTag(@RequestBody @Valid TagRequestDTO tagRequestDTO, BindingResult bindingResult){
        validate(tagRequestDTO, bindingResult);
        return tagsService.update(tagRequestDTO);
    }

    private void validate(TagRequestDTO tagRequestDTO, BindingResult bindingResult){
        tagValidator.validate(tagRequestDTO, bindingResult);
        if (bindingResult.hasFieldErrors()){
            throw new ValidationException(bindingResult);
        }
    }
}
