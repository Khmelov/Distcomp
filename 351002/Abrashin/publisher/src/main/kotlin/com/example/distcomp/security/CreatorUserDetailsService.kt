package com.example.distcomp.security

import com.example.distcomp.repository.CreatorRepository
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class CreatorUserDetailsService(
    private val creatorRepository: CreatorRepository
) : UserDetailsService {
    override fun loadUserByUsername(username: String): UserDetails {
        val creator = creatorRepository.findByLogin(username)
            ?: throw UsernameNotFoundException("Creator with login $username not found")

        return User(
            creator.login ?: throw UsernameNotFoundException("Creator login is empty"),
            creator.password ?: throw UsernameNotFoundException("Creator password is empty"),
            listOf(SimpleGrantedAuthority("ROLE_${creator.role.name}"))
        )
    }
}
