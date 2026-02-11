package com.distcomp.repository.user

interface CrudRepository<T> {
    fun save(user: T)

    fun findById(id: Long): T?

    fun findAll(): List<T>

    fun removeById(id: Long)
}