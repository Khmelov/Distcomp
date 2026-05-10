package com.example.entitiesapp.controller

import com.example.entitiesapp.util.JwtUtils
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v2.0")
class AuthController(
    private val authenticationManager: AuthenticationManager,
    private val jwtUtils: JwtUtils
) {
    @PostMapping("/login")
    fun login(@RequestBody authRequest: Map<String, String>): Map<String, String> {
        val login = authRequest["login"] ?: ""
        val password = authRequest["password"] ?: ""

        val auth = authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(login, password)
        )

        val role = auth.authorities.first().authority
        val token = jwtUtils.generateToken(login, role)

        return mapOf("access_token" to token)
    }
}