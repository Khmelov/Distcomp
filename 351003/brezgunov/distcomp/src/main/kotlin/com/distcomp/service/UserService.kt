package com.distcomp.service

import com.distcomp.mapper.UserMapper
import org.springframework.stereotype.Service

@Service
class UserService (
    val userMapper: UserMapper
) {
//    fun createUser(): UserResponseTo {
//        return UserResponseTo()
//    }
}