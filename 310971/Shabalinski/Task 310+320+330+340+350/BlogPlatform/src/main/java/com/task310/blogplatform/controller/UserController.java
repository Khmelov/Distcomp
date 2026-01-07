package com.task310.blogplatform.controller;

import com.task310.blogplatform.dto.UserRequestTo;
import com.task310.blogplatform.dto.UserResponseTo;
import com.task310.blogplatform.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/users")
    public ResponseEntity<UserResponseTo> createUser(@RequestBody UserRequestTo userRequestTo) {
        UserResponseTo created = userService.create(userRequestTo);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserResponseTo>> getAllUsers() {
        List<UserResponseTo> users = userService.findAll();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<UserResponseTo> getUserById(@PathVariable Long id) {
        UserResponseTo user = userService.findById(id);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<UserResponseTo> updateUser(@PathVariable Long id, @RequestBody UserRequestTo userRequestTo) {
        UserResponseTo updated = userService.update(id, userRequestTo);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}

