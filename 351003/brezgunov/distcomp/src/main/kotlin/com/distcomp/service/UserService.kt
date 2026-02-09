package com.distcomp.service

import com.distcomp.dto.user.UserRequestTo
import com.distcomp.dto.user.UserResponseTo
import com.distcomp.mapper.UserMapper
import com.distcomp.repository.user.UserRepository
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service

@Service
class UserService (
    val userMapper: UserMapper,
    @Qualifier("userRepositoryInMem") val userRepository: UserRepository
) {
    fun createUser(userRequestTo: UserRequestTo): UserResponseTo {
        val user = userMapper.toUserEntity(userRequestTo)
        userRepository.save(user)
        return userMapper.toUserResponse(user)
    }
}