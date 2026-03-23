package com.example.distcomp.repository

import com.example.distcomp.model.BaseEntity

interface CrudRepository<T : BaseEntity, ID> {
    fun save(entity: T): T
    fun findById(id: ID): T?
    fun findAll(): List<T>
    fun deleteById(id: ID): Boolean
    fun existsById(id: ID): Boolean
}
