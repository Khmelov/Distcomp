package by.distcomp.task1.controller;

import by.distcomp.task1.dto.UserRequestTo;
import by.distcomp.task1.dto.UserResponseTo;
import by.distcomp.task1.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1.0/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<UserResponseTo> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{user-id}")
    public UserResponseTo getUser(@PathVariable("user-id") Long userId) {

        return userService.getUserById(userId);
    }

    @PostMapping
    public ResponseEntity<UserResponseTo> createUser(@Valid @RequestBody UserRequestTo request) {
        UserResponseTo createdUser = userService.createUser(request);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdUser.id())
                .toUri();

        return ResponseEntity
                .created(location)
                .body(createdUser);
    }

    @PutMapping("/{user-id}")
    public ResponseEntity<UserResponseTo> updateUser(@PathVariable("user-id") Long userId, @Valid @RequestBody UserRequestTo request) {
        UserResponseTo user = userService.updateUser(userId, request);
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/{user-id}")
    public ResponseEntity<Void> deleteUser(@PathVariable("user-id") Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }
}
