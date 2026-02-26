package com.distcomp.repository

import com.distcomp.entity.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, Long> {
    fun existsByLogin(login: String): Boolean
}
