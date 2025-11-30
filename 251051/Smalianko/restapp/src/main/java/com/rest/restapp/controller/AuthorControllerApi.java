package com.rest.restapp.controller;

import com.rest.restapp.dto.request.AuthorRequestToDto;
import com.rest.restapp.dto.response.AuthorResponseToDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Tag(name = "Authors", description = "CRUD operations for Authors")
public interface AuthorControllerApi {

    @Operation(summary = "Create Author")
    @ApiResponse(responseCode = "201", description = "Author создан")
    @PostMapping("/authors")
    ResponseEntity<AuthorResponseToDto> createAuthor(@Valid  @RequestBody AuthorRequestToDto requestTo);

    @Operation(summary = "Get Author by id")
    @ApiResponse(responseCode = "200", description = "Author найден")
    @GetMapping("/authors/{id}")
    ResponseEntity<AuthorResponseToDto> getAuthorById(@PathVariable Long id);

    @Operation(summary = "Get all Authors")
    @ApiResponse(responseCode = "200", description = "Все authors")
    @GetMapping("/authors")
    ResponseEntity<List<AuthorResponseToDto>> getAllAuthors();

    @Operation(summary = "Update Author by id")
    @ApiResponse(responseCode = "200", description = "Author обновлен")
    @PutMapping("/authors/{id}")
    ResponseEntity<AuthorResponseToDto> updateAuthor(@PathVariable Long id,
                                                  @Valid @RequestBody AuthorRequestToDto requestTo);

    @Operation(summary = "Delete Author by id")
    @ApiResponse(responseCode = "204", description = "Author удалён")
    @DeleteMapping("/authors/{id}")
    ResponseEntity<Void> deleteAuthor(@PathVariable Long id);
}