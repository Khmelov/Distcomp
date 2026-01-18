package com.rest.restapp.controller;

import com.rest.restapp.dto.request.IssueRequestToDto;
import com.rest.restapp.dto.response.UserResponseTo;
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

    @Operation(summary = "Create Issue", description = "Create Issue")
    @ApiResponse(responseCode = "201", description = "Create Issue",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = IssueResponseToDto.class)))
    @PostMapping("/issues")
    ResponseEntity<IssueResponseToDto> createIssue(@Valid @RequestBody IssueRequestToDto requestTo);

    @Operation(summary = "Get Issue by id")
    @ApiResponse(responseCode = "200", description = "Get Issue")
    @GetMapping("/issues/{id}")
    ResponseEntity<IssueResponseToDto> getIssueById(@PathVariable Long id);

    @Operation(summary = "Get all Issues")
    @ApiResponse(responseCode = "200", description = "Get all issues")
    @GetMapping("/issues")
    ResponseEntity<List<IssueResponseToDto>> getAllIssues();

    @Operation(summary = "Update Issue by id")
    @ApiResponse(responseCode = "200", description = "Update Issue")
    @PutMapping("/issues/{id}")
    ResponseEntity<IssueResponseToDto> updateIssue(@PathVariable Long id,
                                                   @Valid @RequestBody IssueRequestToDto requestTo);

    @Operation(summary = "Delete Issue by id")
    @ApiResponse(responseCode = "204", description = "Delete Issue")
    @DeleteMapping("/issues/{id}")
    ResponseEntity<Void> deleteIssue(@PathVariable Long id);

    @Operation(summary = "Get User of Issue", description = "Get User")
    @ApiResponse(responseCode = "200", description = "Get User")
    @GetMapping("/issues/{id}/user")
    ResponseEntity<UserResponseTo> getUserByIssueId(@PathVariable Long id);
}