package com.example.discussion.controller;

import com.example.discussion.dto.UserRequestTo;
import com.example.discussion.dto.UserResponseTo;
import com.example.discussion.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.List;

@RestController
@RequestMapping("/api/v1.0/users")
public class UserController {

    private final UserService userService;
    private final RedisTemplate<String, Object> redisTemplate;

    @Autowired
    public UserController(UserService userService, RedisTemplate<String, Object> redisTemplate) {
        this.userService = userService;
        this.redisTemplate = redisTemplate;
    }

    @GetMapping
    public ResponseEntity<?> getAllUsers() {
        try {
            String cacheKey = "allUsers";
            List<UserResponseTo> users = (List<UserResponseTo>) redisTemplate.opsForValue().get(cacheKey);

            if (users == null) {
                users = userService.findAll();
                redisTemplate.opsForValue().set(cacheKey, users, Duration.ofMinutes(10));
            }
            return new ResponseEntity<>(users, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error fetching users", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        try {
            String cacheKey = "user:" + id;
            UserResponseTo user = (UserResponseTo) redisTemplate.opsForValue().get(cacheKey);

            if (user == null) {
                user = userService.findById(id);
                if (user != null) {
                    redisTemplate.opsForValue().set(cacheKey, user, Duration.ofMinutes(10));
                } else {
                    return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
                }
            }
            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping
    public ResponseEntity<?> createUser(@Valid @RequestBody UserRequestTo userRequestTo) {
        UserResponseTo savedUser = userService.save(userRequestTo);
        redisTemplate.delete("allUsers"); // Инвалидация кэша
        return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<?> updateUser(@Valid @RequestBody UserRequestTo userRequestTo) {
        try {
            UserResponseTo updatedUser = userService.update(userRequestTo);
            if (updatedUser != null) {
                redisTemplate.delete("allUsers");
                redisTemplate.delete("user:" + updatedUser.getId());
                return new ResponseEntity<>(updatedUser, HttpStatus.OK);
            }
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Error updating user", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteById(id);
            redisTemplate.delete("allUsers");
            redisTemplate.delete("user:" + id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>("Error deleting user", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}