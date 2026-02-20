package com.distcomp.controller

import com.distcomp.dto.user.UserRequestTo
import com.distcomp.dto.user.UserResponseTo
import com.distcomp.service.UserService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/{version}/users")
class UserController (
    private val userService: UserService
) {
    @PostMapping(version = "1.0")
    @ResponseStatus(HttpStatus.CREATED)
    fun createUser(@RequestBody userRequestTo: UserRequestTo): UserResponseTo {
        return userService.createUser(userRequestTo)
    }

    @GetMapping("{id}")
    fun readUserById(@PathVariable("id") id: Long): UserResponseTo {
        return userService.readUserById(id)
    }

    @GetMapping
    fun findAll(): List<UserResponseTo> {
        return userService.readAll()
    }

    @PutMapping(version = "1.0")
    @ResponseStatus(HttpStatus.OK)
    fun updateUser(@RequestBody userRequestTo: UserRequestTo): UserResponseTo {
        return userService.updateUser(userRequestTo)
    }

    @DeleteMapping(path = ["/{id}"], version = "1.0")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteUser(@PathVariable("id") id: Long) {
        userService.removeUserById(id)
    }
}