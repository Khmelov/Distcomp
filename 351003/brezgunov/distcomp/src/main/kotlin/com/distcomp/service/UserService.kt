package com.distcomp.service

import com.distcomp.dto.user.UserRequestTo
import com.distcomp.dto.user.UserResponseTo
import com.distcomp.entity.User
import com.distcomp.exception.UserNotFoundException
import com.distcomp.mapper.UserMapper
import com.distcomp.repository.CrudRepository
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service

@Service
class UserService (
    val userMapper: UserMapper,
    @Qualifier("userRepositoryInMem") val userRepository: CrudRepository<User>
) {
    fun createUser(userRequestTo: UserRequestTo): UserResponseTo {
        val user = userMapper.toUserEntity(userRequestTo)
        userRepository.save(user)
        return userMapper.toUserResponse(user)
    }

    fun readUserById(id: Long): UserResponseTo {
        val user = userRepository.findById(id) ?: throw UserNotFoundException("User not found")
        return userMapper.toUserResponse(user)
    }

    fun readAll(): List<UserResponseTo> {
        return userRepository.findAll().map { userMapper.toUserResponse(it) }
    }

    fun updateUser(userRequestTo: UserRequestTo): UserResponseTo {
        if (userRequestTo.id == null || userRepository.findById(userRequestTo.id) == null) {
            throw UserNotFoundException("User not found")
        }

        val user = userMapper.toUserEntity(userRequestTo)
        userRepository.save(user)
        return userMapper.toUserResponse(user)
    }

    fun removeUserById(id: Long) {
        if (userRepository.findById(id) == null) {
            throw UserNotFoundException("User not found")
        }

        userRepository.removeById(id)
    }
}