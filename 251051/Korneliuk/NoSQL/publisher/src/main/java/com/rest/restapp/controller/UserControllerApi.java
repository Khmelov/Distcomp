package com.rest.restapp.controller;

import com.rest.restapp.dto.request.UserRequestToDto;
import com.rest.restapp.dto.response.UserResponseTo;
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

@Tag(name = "Users", description = "CRUD operations for Users")
public interface UserControllerApi {

    @Operation(summary = "Create User")
    @ApiResponse(responseCode = "201", description = "Create User")
    @PostMapping("/users")
    ResponseEntity<UserResponseTo> createUser(@Valid  @RequestBody UserRequestToDto requestTo);

    @Operation(summary = "Get User by id")
    @ApiResponse(responseCode = "200", description = "Get User")
    @GetMapping("/users/{id}")
    ResponseEntity<UserResponseTo> getUserById(@PathVariable Long id);

    @Operation(summary = "Get all Users")
    @ApiResponse(responseCode = "200", description = "Get all Users")
    @GetMapping("/users")
    ResponseEntity<List<UserResponseTo>> getAllUsers();

    @Operation(summary = "Update User by id")
    @ApiResponse(responseCode = "200", description = "Update User")
    @PutMapping("/users/{id}")
    ResponseEntity<UserResponseTo> updateUser(@PathVariable Long id,
                                              @Valid @RequestBody UserRequestToDto requestTo);

    @Operation(summary = "Delete User by id")
    @ApiResponse(responseCode = "204", description = "Delete User")
    @DeleteMapping("/users/{id}")
    ResponseEntity<Void> deleteUser(@PathVariable Long id);
}