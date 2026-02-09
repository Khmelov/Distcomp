package com.distcomp.repository.user

import com.distcomp.entity.User
import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Repository
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong

@Repository
class UserRepositoryInMem : UserRepository {
    private val userMap = ConcurrentHashMap<Long, User>()
    private val counter = AtomicLong(0L)

    override fun save(user: User) {
        val index = counter.incrementAndGet()
        userMap[index] = user
        user.id = index
    }

    override fun findUserById(id: Long): User? {
        return userMap[id]
    }
}
