package com.distcomp.controller

import com.distcomp.dto.user.UserRequestTo
import com.distcomp.dto.user.UserResponseTo
import com.distcomp.service.UserService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v2.0/users")
class UserV2Controller(
    private val userService: UserService
) {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createUser(@Valid @RequestBody userRequestTo: UserRequestTo): UserResponseTo =
        userService.createUser(userRequestTo)

    @GetMapping("/{id}")
    fun readUserById(@PathVariable id: Long): UserResponseTo =
        userService.readUserById(id)

    @GetMapping
    fun findAll(): List<UserResponseTo> =
        userService.readAll()

    @PutMapping
    fun updateUser(@Valid @RequestBody userRequestTo: UserRequestTo): UserResponseTo =
        userService.updateUser(userRequestTo)

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteUser(@PathVariable id: Long) {
        userService.removeUserById(id)
    }
}