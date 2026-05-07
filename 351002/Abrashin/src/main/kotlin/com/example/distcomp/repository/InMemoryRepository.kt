package com.example.distcomp.repository

import com.example.distcomp.model.BaseEntity
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong

abstract class InMemoryRepository<T : BaseEntity> : CrudRepository<T, Long> {
    protected val storage = ConcurrentHashMap<Long, T>()
    private val idCounter = AtomicLong(1)

    override fun save(entity: T): T {
        if (entity.id == null) {
            entity.id = idCounter.getAndIncrement()
        }
        storage[entity.id!!] = entity
        return entity
    }

    override fun findById(id: Long): T? = storage[id]

    override fun findAll(): List<T> = storage.values.toList()

    override fun deleteById(id: Long): Boolean = storage.remove(id) != null

    override fun existsById(id: Long): Boolean = storage.containsKey(id)
}
