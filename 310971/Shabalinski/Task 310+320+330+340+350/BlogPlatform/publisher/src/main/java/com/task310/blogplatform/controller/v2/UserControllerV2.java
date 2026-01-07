package com.task310.blogplatform.controller.v2;

import com.task310.blogplatform.dto.UserRequestTo;
import com.task310.blogplatform.dto.UserResponseTo;
import com.task310.blogplatform.model.Role;
import com.task310.blogplatform.service.CurrentUserService;
import com.task310.blogplatform.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v2.0")
public class UserControllerV2 {
    private final UserService userService;
    private final CurrentUserService currentUserService;

    @Autowired
    public UserControllerV2(UserService userService, CurrentUserService currentUserService) {
        this.userService = userService;
        this.currentUserService = currentUserService;
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponseTo>> getAllUsers() {
        List<UserResponseTo> users = userService.findAll();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('CUSTOMER') and @currentUserService.isCurrentUser(#id))")
    public ResponseEntity<UserResponseTo> getUserById(@PathVariable Long id) {
        UserResponseTo user = userService.findById(id);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('CUSTOMER') and @currentUserService.isCurrentUser(#id))")
    public ResponseEntity<UserResponseTo> updateUser(@PathVariable Long id, @RequestBody UserRequestTo userRequestTo) {
        UserResponseTo updated = userService.update(id, userRequestTo);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('CUSTOMER') and @currentUserService.isCurrentUser(#id))")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}

