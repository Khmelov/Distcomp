package com.distcomp.service

import com.distcomp.dto.user.UserRequestTo
import com.distcomp.dto.user.UserResponseTo
import com.distcomp.exception.DuplicateUserException
import com.distcomp.exception.UserNotFoundException
import com.distcomp.mapper.UserMapper
import com.distcomp.repository.UserRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(
    val userMapper: UserMapper,
    val userRepository: UserRepository
) {
    @Transactional
    fun createUser(userRequestTo: UserRequestTo): UserResponseTo {
        if (userRepository.existsByLogin(userRequestTo.login)) {
            throw DuplicateUserException("User with login already exists")
        }

        val user = userMapper.toUserEntity(userRequestTo)
        userRepository.save(user)
        return userMapper.toUserResponse(user)
    }

    fun readUserById(id: Long): UserResponseTo {
        val user = userRepository.findByIdOrNull(id) ?: throw UserNotFoundException("User not found")
        return userMapper.toUserResponse(user)
    }

    fun readAll(): List<UserResponseTo> {
        return userRepository.findAll().map { userMapper.toUserResponse(it) }
    }

    @Transactional
    fun updateUser(userRequestTo: UserRequestTo): UserResponseTo {
        if (userRequestTo.id == null || userRepository.findByIdOrNull(userRequestTo.id) == null) {
            throw UserNotFoundException("User not found")
        }

        val user = userMapper.toUserEntity(userRequestTo)
        userRepository.save(user)
        return userMapper.toUserResponse(user)
    }

    @Transactional
    fun removeUserById(id: Long) {
        if (userRepository.findByIdOrNull(id) == null) {
            throw UserNotFoundException("User not found")
        }

        userRepository.deleteById(id)
    }
}