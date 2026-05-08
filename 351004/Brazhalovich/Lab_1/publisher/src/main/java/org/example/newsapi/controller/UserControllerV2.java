package org.example.newsapi.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.newsapi.dto.request.UserRequestTo;
import org.example.newsapi.dto.response.UserResponseTo;
import org.example.newsapi.service.UserService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v2.0/users")
@RequiredArgsConstructor
public class UserControllerV2 {

    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponseTo> getAll(@PageableDefault(size = 50) Pageable pageable) {
        return userService.findAll(pageable).getContent();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('CUSTOMER') and @userSecurity.isOwner(#id))")
    public UserResponseTo getById(@PathVariable Long id) {
        return userService.findById(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('CUSTOMER') and @userSecurity.isOwner(#id))")
    public UserResponseTo update(@PathVariable Long id, @RequestBody @Valid UserRequestTo request) {
        return userService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN') or (hasRole('CUSTOMER') and @userSecurity.isOwner(#id))")
    public void delete(@PathVariable Long id) {
        userService.delete(id);
    }
}