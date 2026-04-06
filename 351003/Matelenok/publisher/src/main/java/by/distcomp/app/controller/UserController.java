package by.distcomp.app.controller;

import by.distcomp.app.dto.UserRequestTo;
import by.distcomp.app.dto.UserResponseTo;
import by.distcomp.app.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

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
    public List<UserResponseTo> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return userService.getUsersPage(pageable);
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
