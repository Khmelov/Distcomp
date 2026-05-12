package by.bsuir.task350.publisher.controller;

import by.bsuir.task350.publisher.dto.request.UserRequestTo;
import by.bsuir.task350.publisher.dto.response.UserResponseTo;
import by.bsuir.task350.publisher.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<UserResponseTo> create(@Valid @RequestBody UserRequestTo request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.create(request));
    }

    @GetMapping
    public List<UserResponseTo> findAll() {
        return userService.findAll();
    }

    @GetMapping("/{id}")
    public UserResponseTo findById(@PathVariable Long id) {
        return userService.findById(id);
    }

    @PutMapping
    public UserResponseTo update(@Valid @RequestBody UserRequestTo request) {
        return userService.update(request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
