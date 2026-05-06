package com.example.distcomp.controller

import com.example.distcomp.dto.request.LoginRequestTo
import com.example.distcomp.dto.response.AuthTokenResponseTo
import com.example.distcomp.dto.response.CurrentUserResponseTo
import com.example.distcomp.service.AuthService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v2.0")
class AuthController(
    private val authService: AuthService
) {
    @PostMapping("/login")
    fun login(@Valid @RequestBody request: LoginRequestTo): AuthTokenResponseTo =
        authService.login(request)

    @GetMapping("/me")
    fun currentUser(): CurrentUserResponseTo = authService.currentUser()
}
