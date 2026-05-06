package com.example.distcomp.service

import com.example.distcomp.dto.request.LoginRequestTo
import com.example.distcomp.dto.response.AuthTokenResponseTo
import com.example.distcomp.dto.response.CurrentUserResponseTo
import com.example.distcomp.repository.CreatorRepository
import com.example.distcomp.security.CurrentUserService
import com.example.distcomp.security.JwtService
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val authenticationManager: AuthenticationManager,
    private val creatorRepository: CreatorRepository,
    private val jwtService: JwtService,
    private val currentUserService: CurrentUserService
) {
    fun login(request: LoginRequestTo): AuthTokenResponseTo {
        authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(request.login, request.password)
        )

        val creator = creatorRepository.findByLogin(request.login)
            ?: throw BadCredentialsException("Invalid login or password")

        return AuthTokenResponseTo(accessToken = jwtService.generateToken(creator))
    }

    fun currentUser(): CurrentUserResponseTo {
        val creator = currentUserService.currentCreator()
        return CurrentUserResponseTo(
            id = creator.id ?: throw BadCredentialsException("Authenticated creator id not found"),
            login = creator.login.orEmpty(),
            role = creator.role.name,
            firstName = creator.firstname,
            lastName = creator.lastname
        )
    }
}
