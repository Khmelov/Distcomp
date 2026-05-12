package by.bsuir.task361.publisher.controller;

import by.bsuir.task361.publisher.dto.request.UserRequestTo;
import by.bsuir.task361.publisher.dto.response.UserResponseTo;
import by.bsuir.task361.publisher.service.SecuredUserService;
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
@RequestMapping("/api/v2.0/users")
public class UserV2Controller {
    private final SecuredUserService securedUserService;

    public UserV2Controller(SecuredUserService securedUserService) {
        this.securedUserService = securedUserService;
    }

    @PostMapping
    public ResponseEntity<UserResponseTo> register(@Valid @RequestBody UserRequestTo request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(securedUserService.register(request));
    }

    @GetMapping
    public List<UserResponseTo> findAll() {
        return securedUserService.findAll();
    }

    @GetMapping("/{id}")
    public UserResponseTo findById(@PathVariable Long id) {
        return securedUserService.findById(id);
    }

    @PutMapping
    public UserResponseTo update(@Valid @RequestBody UserRequestTo request) {
        return securedUserService.update(request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        securedUserService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
