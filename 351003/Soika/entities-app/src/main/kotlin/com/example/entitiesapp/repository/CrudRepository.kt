package com.example.entitiesapp.repository

interface CrudRepository<T> {

    fun findAll(): List<T>

    fun findById(id: Long): T?

    fun save(entity: T): T

    fun update(id: Long, entity: T): T

    fun delete(id: Long)
}