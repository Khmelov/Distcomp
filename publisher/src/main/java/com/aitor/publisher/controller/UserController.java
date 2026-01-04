package com.aitor.publisher.controller;

import com.aitor.publisher.dto.UserRequestTo;
import com.aitor.publisher.dto.UserResponseTo;
import com.aitor.publisher.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("users")
public class UserController {
    private final UserService service;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping()
    public UserResponseTo add(@RequestBody @Valid UserRequestTo request){
        return service.add(request);
    }

    @PutMapping("/{id}")
    public UserResponseTo set(@PathVariable Long id, @RequestBody @Valid UserRequestTo request){
        return service.set(id, request);
    }

    @GetMapping("/{id}")
    public UserResponseTo get(@PathVariable Long id){
        return service.get(id);
    }

    @GetMapping()
    public List<UserResponseTo> getAll(){
        return service.getAll();
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public UserResponseTo remove(@PathVariable Long id){
        return service.remove(id);
    }
}
