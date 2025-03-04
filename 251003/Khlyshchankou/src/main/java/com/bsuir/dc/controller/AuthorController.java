package com.bsuir.dc.controller;

import com.bsuir.dc.dto.request.AuthorRequestTo;
import com.bsuir.dc.dto.response.AuthorResponseTo;
import com.bsuir.dc.service.AuthorService;
import com.bsuir.dc.util.exception.ValidationException;
import com.bsuir.dc.util.validator.AuthorValidator;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/authors")
public class AuthorController {
    private final AuthorService authorService;
    private final AuthorValidator authorValidator;

    @Autowired
    public AuthorController(AuthorService authorService, AuthorValidator authorValidator) {
        this.authorService = authorService;
        this.authorValidator = authorValidator;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AuthorResponseTo createAuthor(@RequestBody @Valid AuthorRequestTo authorRequestTo, BindingResult bindingResult)
            throws MethodArgumentNotValidException {
        validate(authorRequestTo, bindingResult);
        return authorService.save(authorRequestTo);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<AuthorResponseTo> getAllAuthors() { return authorService.findAll(); }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public AuthorResponseTo getAuthorById(@PathVariable long id) { return authorService.findById(id);}

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public AuthorResponseTo updateAuthor(@RequestBody @Valid AuthorRequestTo authorRequestTo, BindingResult bindingResult){
        validate(authorRequestTo, bindingResult);
        return authorService.update(authorRequestTo);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Long updateCreator(@PathVariable Long id, @RequestBody @Valid AuthorRequestTo authorRequestTo, BindingResult bindingResult){
        validate(authorRequestTo, bindingResult);
        return authorService.update(id, authorRequestTo).getId();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAuthor(@PathVariable long id) { authorService.deleteById(id); }

    private void validate(AuthorRequestTo authorRequestTo, BindingResult bindingResult){
        authorValidator.validate(authorRequestTo, bindingResult);
        if (bindingResult.hasFieldErrors()) { throw new ValidationException(bindingResult); }
    }
}
