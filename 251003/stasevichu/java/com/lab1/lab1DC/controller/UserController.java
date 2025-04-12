package com.lab1.lab1DC.controller;

import com.lab1.lab1DC.dto.StoryResponseTo;
import com.lab1.lab1DC.dto.UserRequestTo;
import com.lab1.lab1DC.dto.UserResponseTo;
import com.lab1.lab1DC.entity.User;
import com.lab1.lab1DC.exception.CustomException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.lab1.lab1DC.service.UserService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/v1.0/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<UserResponseTo> createUser(@RequestBody UserRequestTo userRequestTo) {
        try {
            Long id = System.currentTimeMillis();

            if(!validateUser(userRequestTo)){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new UserResponseTo());
            }
            User user = new User(id, userRequestTo.getLogin(), userRequestTo.getPassword(), userRequestTo.getFirstname(), userRequestTo.getLastname(), new ArrayList<>());
            UserResponseTo response = userService.createUser(user);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (DataIntegrityViolationException e){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new UserResponseTo());
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new UserResponseTo());
        }
    }

    @GetMapping
    public ResponseEntity<List<UserResponseTo>> getAllUsers() {
        try {
            List<UserResponseTo> response = userService.getAllUsers();
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseTo> getUserById(@PathVariable Long id) {
        try {
            UserResponseTo response = userService.getUserById(id);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new UserResponseTo());
        }
    }

    @PutMapping
    public ResponseEntity<UserResponseTo> updateUser(@RequestBody UserRequestTo userRequestTo) {
        System.out.println("Received PUT request with ID: " + userRequestTo.getId());
        try {
            if (userRequestTo.getId() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new UserResponseTo());
            }
            UserResponseTo updatedUser = userService.updateUser(userRequestTo.getId(), userRequestTo);
            return ResponseEntity.ok(updatedUser);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new UserResponseTo());
        }
        catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new UserResponseTo());
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new UserResponseTo());
        }
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.noContent().build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private boolean validateUser(UserRequestTo userRequestTo) {

        if (userRequestTo.getLogin().length() < 2 ||
                userRequestTo.getLogin().length() > 64) {
            return false;
        }
        if (userRequestTo.getFirstname().length() < 2 ||
                userRequestTo.getFirstname().length() > 64) {
            return false;
        }
        if (userRequestTo.getLastname().length() < 2 ||
                userRequestTo.getLastname().length() > 64) {
            return false;
        }
        if (userRequestTo.getPassword().length() < 8 ||
                userRequestTo.getPassword().length() > 128) {
            return false;
        }

        return true;
    }
}
