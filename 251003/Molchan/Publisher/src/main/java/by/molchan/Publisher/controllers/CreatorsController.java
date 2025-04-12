package by.molchan.Publisher.controllers;

import by.molchan.Publisher.DTOs.Requests.CreatorRequestDTO;
import by.molchan.Publisher.DTOs.Responses.CreatorResponseDTO;
import by.molchan.Publisher.services.CreatorsService;
import by.molchan.Publisher.utils.CreatorValidator;
import by.molchan.Publisher.utils.exceptions.ValidationException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/creators")
public class    CreatorsController {
    private final CreatorsService creatorsService;
    private final CreatorValidator creatorValidator;
    @Autowired
    public CreatorsController(CreatorsService creatorsService, CreatorValidator validator) {
        this.creatorsService = creatorsService;
        this.creatorValidator = validator;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CreatorResponseDTO createCreator(@RequestBody @Valid CreatorRequestDTO creatorRequestDTO, BindingResult bindingResult) throws MethodArgumentNotValidException {
        validate(creatorRequestDTO, bindingResult);
        return creatorsService.save(creatorRequestDTO);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCreator(@PathVariable long id){
        creatorsService.deleteById(id);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CreatorResponseDTO> getAllCreators(){
        return creatorsService.findAll();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public CreatorResponseDTO getCreator(@PathVariable long id) {
        return creatorsService.findById(id);
    }

    // Because of test limitations we don't require id in the path, and also we send creator in the response,
    // but we shouldn't do that
    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public CreatorResponseDTO updateCreator(@RequestBody @Valid CreatorRequestDTO creatorRequestDTO, BindingResult bindingResult){
        validate(creatorRequestDTO, bindingResult);
        return creatorsService.update(creatorRequestDTO);
    }

    // Rest version
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Long updateCreator(@PathVariable Long id, @RequestBody @Valid CreatorRequestDTO creatorRequestDTO, BindingResult bindingResult){
        validate(creatorRequestDTO, bindingResult);
        return creatorsService.update(id, creatorRequestDTO).getId();
    }

    private void validate(CreatorRequestDTO creatorRequestDTO, BindingResult bindingResult){
        creatorValidator.validate(creatorRequestDTO, bindingResult);
        if (bindingResult.hasFieldErrors()) {
            throw new ValidationException(bindingResult);
        }
    }
}
