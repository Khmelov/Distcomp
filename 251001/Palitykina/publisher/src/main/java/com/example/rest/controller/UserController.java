package com.example.rest.controller;

import com.example.rest.dto.requestDto.UserRequestTo;
import com.example.rest.dto.responseDto.UserResponseTo;
import com.example.rest.dto.updateDto.UserUpdateTo;
import com.example.rest.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.Collection;
import java.util.NoSuchElementException;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1.0/users")
@RequiredArgsConstructor
public class    UserController {

    private final UserService userService;

    @GetMapping
    public Collection<UserResponseTo> getAll() { return userService.getAll(); }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponseTo create(@RequestBody @Valid UserRequestTo input) {
        return userService.create(input);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public UserResponseTo update(@RequestBody @Valid UserUpdateTo input) {
        try{ return userService.update(input); }
        catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{id}")
    public UserResponseTo get(@PathVariable long id) {
        try{ return userService.get(id); }
        catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable long id) {
        boolean deleted = userService.delete(id);
        if (!deleted) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }
}
