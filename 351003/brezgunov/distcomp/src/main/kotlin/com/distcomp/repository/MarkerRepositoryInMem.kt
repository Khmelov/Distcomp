package com.distcomp.repository

import com.distcomp.entity.Marker
import org.springframework.stereotype.Repository
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong

@Repository
class MarkerRepositoryInMem : CrudRepository<Marker> {
    private val markerMap = ConcurrentHashMap<Long, Marker>()
    private val counter = AtomicLong(0L)

    override fun save(entity: Marker) {
        val index = if (entity.id == null) {
            val newId = counter.incrementAndGet()
            entity.id = newId
            newId
        } else {
            entity.id!!
        }

        markerMap[index] = entity
    }

    override fun findById(id: Long): Marker? {
        return markerMap[id]
    }

    override fun findAll(): List<Marker> {
        return markerMap.values.toList()
    }

    override fun removeById(id: Long) {
        markerMap.remove(id)
    }
}