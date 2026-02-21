package com.distcomp.repository

interface CrudRepository<T> {
    fun save(entity: T)

    fun findById(id: Long): T?

    fun findAll(): List<T>

    fun removeById(id: Long)
}