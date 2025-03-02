package bsuir.dc.rest.repository

import bsuir.dc.rest.entity.Identifiable

interface Repository<T : Identifiable> {
    fun findById(id: Long): T
    fun findAll(): List<T>
    fun save(entity: T): T
    fun update(entity: T): T
    fun deleteById(id: Long)
}
