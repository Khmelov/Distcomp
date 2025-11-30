package com.rest.restapp.controller;

import com.rest.restapp.dto.request.TagRequestToDto;
import com.rest.restapp.dto.response.TagResponseToDto;
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

@Tag(name = "Tags", description = "CRUD operations for Tags")
public interface TagControllerApi {

    @Operation(summary = "Create Tag")
    @ApiResponse(responseCode = "201", description = "Tag создан")
    @PostMapping("/tags")
    ResponseEntity<TagResponseToDto> createTag(@Valid @RequestBody TagRequestToDto requestTo);

    @Operation(summary = "Get Tag by id")
    @ApiResponse(responseCode = "200", description = "Tag найден")
    @GetMapping("/tags/{id}")
    ResponseEntity<TagResponseToDto> getTagById(@PathVariable Long id);

    @Operation(summary = "Get all Tags")
    @ApiResponse(responseCode = "200", description = "Все tags")
    @GetMapping("/tags")
    ResponseEntity<List<TagResponseToDto>> getAllTags();

    @Operation(summary = "Update Tag by id")
    @ApiResponse(responseCode = "200", description = "Tag обновлен")
    @PutMapping("/tags/{id}")
    ResponseEntity<TagResponseToDto> updateTag(@PathVariable Long id,
                                               @Valid @RequestBody TagRequestToDto requestTo);

    @Operation(summary = "Delete Tag by id")
    @ApiResponse(responseCode = "204", description = "Tag удалён")
    @DeleteMapping("/tags/{id}")
    ResponseEntity<Void> deleteTag(@PathVariable Long id);
}