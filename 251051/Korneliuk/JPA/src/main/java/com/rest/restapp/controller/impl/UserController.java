package com.rest.restapp.controller.impl;

import com.rest.restapp.controller.UserControllerApi;
import com.rest.restapp.dto.request.UserRequest;
import com.rest.restapp.dto.response.UserResponseTo;
import com.rest.restapp.service.UserService;
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
public class UserController implements UserControllerApi {

    UserService userService;

    @Override
    public ResponseEntity<UserResponseTo> createUser(UserRequest requestTo) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        userService.createUser(requestTo)
                );
    }

    @Override
    public ResponseEntity<UserResponseTo> getUserById(Long id) {
        return ResponseEntity
                .ok(
                        userService.getUserById(id)
                );
    }

    @Override
    public ResponseEntity<List<UserResponseTo>> getAllUsers() {
        return ResponseEntity
                .ok(
                        userService.getAllUsers()
                );
    }

    @Override
    public ResponseEntity<UserResponseTo> updateUser(Long id, UserRequest requestTo) {
        return ResponseEntity.ok(
                userService
                        .updateUser(
                                id,
                                requestTo
                        )
        );
    }

    @Override
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity
                .noContent()
                .build();
    }
}