package by.kapinskiy.Task310.controllers;


import by.kapinskiy.Task310.DTOs.Requests.IssueRequestDTO;
import by.kapinskiy.Task310.DTOs.Requests.NoteRequestDTO;
import by.kapinskiy.Task310.DTOs.Requests.TagRequestDTO;
import by.kapinskiy.Task310.DTOs.Responses.NoteResponseDTO;
import by.kapinskiy.Task310.DTOs.Responses.TagResponseDTO;
import by.kapinskiy.Task310.models.Note;
import by.kapinskiy.Task310.models.Tag;
import by.kapinskiy.Task310.services.NotesService;
import by.kapinskiy.Task310.services.TagsService;
import by.kapinskiy.Task310.utils.Mapper;
import by.kapinskiy.Task310.utils.TagValidator;
import by.kapinskiy.Task310.utils.exceptions.ValidationException;
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
    private final Mapper mapper;
    private final TagsService tagsService;

    private final TagValidator tagValidator;

    @Autowired
    public TagsController(Mapper mapper, TagsService tagsService, TagValidator tagValidator) {
        this.mapper = mapper;
        this.tagsService = tagsService;
        this.tagValidator = tagValidator;
    }


    @PostMapping
    public ResponseEntity<TagResponseDTO> createTag(@RequestBody @Valid TagRequestDTO tagRequestDTO, BindingResult bindingResult) {
        validate(tagRequestDTO, bindingResult);
        Tag tag = mapper.tagRequestToTag(tagRequestDTO);
        return new ResponseEntity<>(mapper.tagToTagResponse(tagsService.save(tag)), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<TagResponseDTO>> getAllTags() {
        return new ResponseEntity<>(mapper.tagsToTagResponses(tagsService.findAll()), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TagResponseDTO> getTagById(@PathVariable Long id) {
        return new ResponseEntity<>(mapper.tagToTagResponse(tagsService.findById(id)), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTag(@PathVariable long id){
        tagsService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // The same as in Users Put
    @PutMapping
    public ResponseEntity<TagResponseDTO> updateTag(@RequestBody @Valid TagRequestDTO tagRequestDTO, BindingResult bindingResult){
        validate(tagRequestDTO, bindingResult);
        Tag updatedTag = tagsService.update(mapper.tagRequestToTag(tagRequestDTO));
        return new ResponseEntity<>(mapper.tagToTagResponse(updatedTag), HttpStatus.OK);
    }

    private void validate(TagRequestDTO tagRequestDTO, BindingResult bindingResult){
        tagValidator.validate(tagRequestDTO, bindingResult);
        if (bindingResult.hasFieldErrors()){
            throw new ValidationException(bindingResult);
        }
    }
}
