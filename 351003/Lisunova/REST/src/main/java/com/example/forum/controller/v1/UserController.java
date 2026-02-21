package com.example.forum.controller.v1;

import com.example.forum.dto.request.UserRequestTo;
import com.example.forum.dto.response.UserResponseTo;
import com.example.forum.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/users")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponseTo create(@Valid @RequestBody UserRequestTo request) {
        return service.create(request);
    }

    @GetMapping("/{id}")
    public UserResponseTo getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @GetMapping
    public List<UserResponseTo> getAll() {
        return service.getAll();
    }

    @PutMapping("/{id}")
    public UserResponseTo update(@PathVariable Long id,
                                 @Valid @RequestBody UserRequestTo request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
