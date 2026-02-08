package com.distcomp.repository.user

import com.distcomp.entity.User

interface UserRepository {
    fun save(user: User)

    fun findUserById(id: Long): User?
}