package by.boukhvalova.distcomp.controllers;

import by.boukhvalova.distcomp.dto.UserRequestTo;
import by.boukhvalova.distcomp.dto.UserResponseTo;
import by.boukhvalova.distcomp.services.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping({"/api/v1.0/users", "/api/v1.0/creators"})
@AllArgsConstructor
public class UserController {
    private final UserService serviceImpl;

    @GetMapping
    public Collection<UserResponseTo> getAll(){
        return serviceImpl.getAll();
    }

    @GetMapping("/{id}")
    public UserResponseTo getById(@PathVariable Long id){
        return serviceImpl.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponseTo create(@RequestBody @Valid UserRequestTo request){
        return serviceImpl.create(request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id){
        serviceImpl.delete(id);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public UserResponseTo update(@RequestBody @Valid UserRequestTo request){
        return serviceImpl.update(request);
    }
}
