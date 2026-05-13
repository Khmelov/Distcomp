package org.polozkov.controller.secured.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.polozkov.dto.user.UserRequestTo;
import org.polozkov.dto.user.UserResponseTo;
import org.polozkov.service.user.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v2.0/users")
@RequiredArgsConstructor
public class UserControllerSecured {

    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponseTo> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or authentication.principal == @userService.getUser(#id).login")
    public UserResponseTo getUser(@PathVariable Long id) {
        return userService.getUser(id);
    }

    @PostMapping // Регистрация (открыта в SecurityConfig)
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponseTo createUser(@Valid @RequestBody UserRequestTo userRequest) {
        return userService.createUser(userRequest);
    }

    @PutMapping
    @PreAuthorize("hasRole('ADMIN') or authentication.principal == #userRequest.login")
    public UserResponseTo updateUser(@Valid @RequestBody UserRequestTo userRequest) {
        return userService.updateUser(userRequest);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }
}