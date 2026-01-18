package com.rest.restapp.controller;

import com.rest.restapp.dto.request.TagRequestTo;
import com.rest.restapp.dto.response.TagResponseTo;
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
    @ApiResponse(responseCode = "201", description = "Create Tag")
    @PostMapping("/tags")
    ResponseEntity<TagResponseTo> createTag(@Valid @RequestBody TagRequestTo requestTo);

    @Operation(summary = "Get Tag by id")
    @ApiResponse(responseCode = "200", description = "Find Tag")
    @GetMapping("/tags/{id}")
    ResponseEntity<TagResponseTo> getTagById(@PathVariable Long id);

    @Operation(summary = "Get all Tags")
    @ApiResponse(responseCode = "200", description = "Get all tags")
    @GetMapping("/tags")
    ResponseEntity<List<TagResponseTo>> getAllTags();

    @Operation(summary = "Update Tag by id")
    @ApiResponse(responseCode = "200", description = "Update Tag")
    @PutMapping("/tags/{id}")
    ResponseEntity<TagResponseTo> updateTag(@PathVariable Long id,
                                            @Valid @RequestBody TagRequestTo requestTo);

    @Operation(summary = "Delete Tag by id")
    @ApiResponse(responseCode = "204", description = "Delete Tag")
    @DeleteMapping("/tags/{id}")
    ResponseEntity<Void> deleteTag(@PathVariable Long id);
}