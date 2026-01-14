package com.socialnetwork.controller.v2;

import com.socialnetwork.dto.request.UserRequestTo;
import com.socialnetwork.dto.response.UserResponseTo;
import com.socialnetwork.exception.UnauthorizedException;
import com.socialnetwork.security.SecurityUtil;
import com.socialnetwork.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v2.0/users")
public class UserControllerV2 {

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<List<UserResponseTo>> getAllUsers() {
        // ADMIN - полный доступ, CUSTOMER - только чтение
        List<UserResponseTo> users = userService.getAll();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseTo> getUserById(@PathVariable Long id) {
        // ADMIN - полный доступ, CUSTOMER - только чтение
        UserResponseTo user = userService.getById(id);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponseTo> updateUser(@PathVariable Long id,
                                                     @Valid @RequestBody UserRequestTo request) {
        // ADMIN - полный доступ, CUSTOMER - только свои данные
        if (SecurityUtil.isCustomer() && !SecurityUtil.isOwner(id)) {
            throw new UnauthorizedException("You can only update your own profile");
        }
        UserResponseTo updatedUser = userService.update(id, request);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        // ADMIN - полный доступ, CUSTOMER - только свои данные
        if (SecurityUtil.isCustomer() && !SecurityUtil.isOwner(id)) {
            throw new UnauthorizedException("You can only delete your own profile");
        }
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/page")
    public ResponseEntity<Page<UserResponseTo>> getUsersPage(
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        // ADMIN - полный доступ, CUSTOMER - только чтение
        Page<UserResponseTo> users = userService.getAll(pageable);
        return ResponseEntity.ok(users);
    }
}

