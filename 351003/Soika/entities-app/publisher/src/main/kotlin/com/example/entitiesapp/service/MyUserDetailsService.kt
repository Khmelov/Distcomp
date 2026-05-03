package com.example.entitiesapp.service

import com.example.entitiesapp.repository.WriterRepository
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class MyUserDetailsService(private val writerRepository: WriterRepository) : UserDetailsService {

    override fun loadUserByUsername(login: String): UserDetails {
        val writer = writerRepository.findByLogin(login)
            ?: throw UsernameNotFoundException("User not found with login: $login")

        return User(
            writer.login,
            writer.password,
            listOf(SimpleGrantedAuthority("ROLE_${writer.role.name}"))
        )
    }
}