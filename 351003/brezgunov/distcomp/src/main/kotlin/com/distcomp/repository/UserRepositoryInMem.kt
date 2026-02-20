package com.distcomp.repository

import com.distcomp.entity.User
import org.springframework.stereotype.Repository
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong

@Repository
class UserRepositoryInMem : CrudRepository<User> {
    private val userMap = ConcurrentHashMap<Long, User>()
    private val counter = AtomicLong(0L)

    override fun save(user: User) {
        val index = if (user.id == null) {
            val newId = counter.incrementAndGet()
            user.id = newId
            newId
        } else {
            user.id!!
        }

        userMap[index] = user
    }

    override fun findById(id: Long): User? {
        return userMap[id]
    }

    override fun findAll(): List<User> {
        return userMap.values.toList()
    }

    override fun removeById(id: Long) {
        userMap.remove(id)
    }
}
