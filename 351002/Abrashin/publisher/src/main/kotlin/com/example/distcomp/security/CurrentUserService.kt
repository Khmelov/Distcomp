package com.example.distcomp.security

import com.example.distcomp.model.Creator
import com.example.distcomp.model.CreatorRole
import com.example.distcomp.repository.CreatorRepository
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.authentication.AnonymousAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service

@Service
class CurrentUserService(
    private val creatorRepository: CreatorRepository
) {
    fun currentLogin(): String {
        val authentication = SecurityContextHolder.getContext().authentication
        if (authentication == null || !authentication.isAuthenticated || authentication is AnonymousAuthenticationToken) {
            throw AccessDeniedException("Authentication required")
        }
        return authentication.name
    }

    fun currentRole(): CreatorRole {
        val authority = SecurityContextHolder.getContext().authentication?.authorities
            ?.firstOrNull()
            ?.authority
            ?.removePrefix("ROLE_")
            ?: throw AccessDeniedException("Role not found")
        return CreatorRole.valueOf(authority)
    }

    fun isAdmin(): Boolean = currentRole() == CreatorRole.ADMIN

    fun currentCreator(): Creator =
        creatorRepository.findByLogin(currentLogin())
            ?: throw AccessDeniedException("Authenticated creator not found")

    fun currentCreatorId(): Long =
        currentCreator().id ?: throw AccessDeniedException("Authenticated creator id not found")
}
