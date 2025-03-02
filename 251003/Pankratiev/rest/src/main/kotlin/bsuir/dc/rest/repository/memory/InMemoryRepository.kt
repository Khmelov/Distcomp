package bsuir.dc.rest.repository.memory

import bsuir.dc.rest.entity.Identifiable
import bsuir.dc.rest.repository.Repository
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong

open class InMemoryRepository<T : Identifiable> : Repository<T> {
    private val storage = ConcurrentHashMap<Long, T>()
    private var idCounter = AtomicLong(1)

    override fun findById(id: Long): T = storage[id]
        ?: throw NoSuchElementException()

    override fun findAll(): List<T> = storage.values.toList()

    override fun save(entity: T): T {
        entity.id = idCounter.getAndIncrement()
        storage[entity.id] = entity
        return entity
    }

    override fun update(entity: T): T {
        validateId(entity.id) {
            storage[entity.id] = entity
        }
        return entity
    }

    override fun deleteById(id: Long) {
        validateId(id) {
            storage.remove(id)
        }
    }

    private fun validateId(id: Long, action: () -> Unit) {
        if (storage.containsKey(id)) {
            action()
        } else {
            throw NoSuchElementException()
        }
    }
}
