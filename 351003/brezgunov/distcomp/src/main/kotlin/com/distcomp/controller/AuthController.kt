package com.distcomp.controller

import com.distcomp.dto.auth.LoginRequest
import com.distcomp.dto.auth.LoginResponse
import com.distcomp.repository.UserRepository
import com.distcomp.security.JwtTokenProvider
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v2.0")
class AuthController(
    private val authenticationManager: AuthenticationManager,
    private val jwtTokenProvider: JwtTokenProvider,
    private val userRepository: UserRepository
) {
    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): LoginResponse {
        authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(request.login, request.password)
        )
        val user = userRepository.findByLogin(request.login)!!
        val token = jwtTokenProvider.generateToken(user.login, user.role.name)
        return LoginResponse(access_token = token)
    }
}