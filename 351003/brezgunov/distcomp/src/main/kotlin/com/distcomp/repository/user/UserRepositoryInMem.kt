package com.distcomp.repository.user

import com.distcomp.entity.User
import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Repository
import java.util.concurrent.atomic.AtomicLong

@Repository
class UserRepositoryInMem : UserRepository {

    private val userMap: MutableMap<Long, User> = mutableMapOf<Long, User>()
    private val counter = AtomicLong(0L)

    @PostConstruct
    fun init() {
        print("init")
    }

    override fun save(user: User) {
        val counter = counter.incrementAndGet()
    }

    override fun findUserById(id: Long): User? {
        return userMap[id]
    }
}
