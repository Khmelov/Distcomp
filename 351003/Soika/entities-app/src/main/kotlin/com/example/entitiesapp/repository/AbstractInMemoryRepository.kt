package com.example.entitiesapp.repository

import com.example.entitiesapp.model.BaseEntity
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong

abstract class AbstractInMemoryRepository<T : BaseEntity> :
    CrudRepository<T> {

    private val storage = ConcurrentHashMap<Long, T>()
    private val idGenerator = AtomicLong(1)

    override fun findAll(): List<T> =
        storage.values.toList()

    override fun findById(id: Long): T? =
        storage[id]

    override fun save(entity: T): T {
        val id = idGenerator.getAndIncrement()
        entity.id = id
        storage[id] = entity
        return entity
    }

    override fun update(id: Long, entity: T): T {
        if (!storage.containsKey(id)) {
            throw RuntimeException("Entity with id=$id not found")
        }
        entity.id = id
        storage[id] = entity
        return entity
    }

    override fun delete(id: Long) {
        if (storage.remove(id) == null) {
            throw RuntimeException("Entity with id=$id not found")
        }
    }
}