package com.rest.restapp.controller.impl;

import com.rest.restapp.controller.AuthorControllerApi;
import com.rest.restapp.dto.request.AuthorRequestToDto;
import com.rest.restapp.dto.response.AuthorResponseToDto;
import com.rest.restapp.service.AuthorService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class AuthorController implements AuthorControllerApi {

    AuthorService authorService;

    @Override
    public ResponseEntity<AuthorResponseToDto> createAuthor(AuthorRequestToDto requestTo) {
        var response = authorService.createAuthor(requestTo);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Override
    public ResponseEntity<AuthorResponseToDto> getAuthorById(Long id) {
        var response = authorService.getAuthorById(id);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<List<AuthorResponseToDto>> getAllAuthors() {
        var responses = authorService.getAllAuthors();
        return ResponseEntity.ok(responses);
    }

    @Override
    public ResponseEntity<AuthorResponseToDto> updateAuthor(Long id,AuthorRequestToDto requestTo) {
        var response = authorService.updateAuthor(id, requestTo);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Void> deleteAuthor(@PathVariable Long id) {
        authorService.deleteAuthor(id);
        return ResponseEntity.noContent().build();
    }
}