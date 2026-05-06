package com.distcomp.security

import com.distcomp.repository.UserRepository
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class UserDetailsServiceImpl(
    private val userRepository: UserRepository
) : UserDetailsService {

    override fun loadUserByUsername(login: String): UserDetails {
        val user = userRepository.findByLogin(login)
            ?: throw UsernameNotFoundException("User not found: $login")

        return User(
            user.login,
            user.password,
            listOf(SimpleGrantedAuthority("ROLE_${user.role}"))
        )
    }
}