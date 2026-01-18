package com.rest.restapp.controller;

import com.rest.restapp.dto.request.IssueRequestToDto;
import com.rest.restapp.dto.response.UserResponseToDto;
import com.rest.restapp.dto.response.IssueResponseToDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
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

@Tag(name = "Issues", description = "CRUD operations for Issues")
public interface IssueControllerApi {

    @Operation(summary = "Create Issue", description = "Creating new Issue")
    @ApiResponse(responseCode = "201", description = "Issue created",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = IssueResponseToDto.class)))
    @PostMapping("/issues")
    ResponseEntity<IssueResponseToDto> createIssue(@Valid @RequestBody IssueRequestToDto requestTo);

    @Operation(summary = "Get Issue by id")
    @ApiResponse(responseCode = "200", description = "Get Issue")
    @GetMapping("/issues/{id}")
    ResponseEntity<IssueResponseToDto> getIssueById(@PathVariable Long id);

    @Operation(summary = "Get all Issues")
    @ApiResponse(responseCode = "200", description = "All issues")
    @GetMapping("/issues")
    ResponseEntity<List<IssueResponseToDto>> getAllIssues();

    @Operation(summary = "Update Issue by id")
    @ApiResponse(responseCode = "200", description = "Issue updated")
    @PutMapping("/issues/{id}")
    ResponseEntity<IssueResponseToDto> updateIssue(@PathVariable Long id,
                                                   @Valid @RequestBody IssueRequestToDto requestTo);

    @Operation(summary = "Delete Issue by id")
    @ApiResponse(responseCode = "204", description = "Issue deleted")
    @DeleteMapping("/issues/{id}")
    ResponseEntity<Void> deleteIssue(@PathVariable Long id);

    @Operation(summary = "Get User of Issue", description = "Get User of Issue")
    @ApiResponse(responseCode = "200", description = "Get user")
    @GetMapping("/issues/{id}/user")
    ResponseEntity<UserResponseToDto> getUserByIssueId(@PathVariable Long id);
}