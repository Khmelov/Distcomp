package com.example.distcomp_1.controller;

import com.example.distcomp_1.mapper.AuthorDto;
import com.example.distcomp_1.mdoel.Author;
import com.example.distcomp_1.repository.AuthorRepository;
import com.example.distcomp_1.service.AuthorService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1.0")
public class AuthorController {

    private final AuthorDto authorDto;
    private final AuthorRepository authorRepository;
    private final AuthorService authorService;

    @Autowired
    public AuthorController(AuthorDto authorDto, AuthorRepository authorRepository, AuthorService authorService) {
        this.authorDto = authorDto;
        this.authorRepository = authorRepository;
        this.authorService = authorService;
    }
    @GetMapping("/authors")
    @ResponseStatus(HttpStatus.OK)
    public List<Author.Out> getAuthors() {
        return authorRepository.getAuthors()
                .stream()
                .map(authorDto::Out)
                .collect(Collectors.toList());
    }

    @PostMapping("/authors")
    @ResponseStatus(HttpStatus.CREATED)
    public Author.Out createAuthor(@RequestBody @Valid Author.In inputDto) {
        Author entity = authorDto.In(inputDto);
        Author saved = authorService.save(entity);
        return authorDto.Out(saved);
    }

    @GetMapping("/authors/{id}")
    public Author.Out getAuthorById(@PathVariable Long id) {
        return authorDto.Out(authorService.findById(id));
    }

    @PutMapping("/authors")
    public ResponseEntity<Author.Out> updateAuthor(@RequestBody @Valid Author.In in) {
        Author author = authorService.findById(in.getId());
        if (author == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            Author newAuthor = authorDto.In(in);
            Author updated = authorService.update(newAuthor);
            return ResponseEntity.ok(authorDto.Out(updated));
        }
    }

    @DeleteMapping("/authors/{id}")
    public ResponseEntity<Void> deleteAuthor(@PathVariable Long id) {
        if (authorService.findById(id) != null) {
            authorService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
