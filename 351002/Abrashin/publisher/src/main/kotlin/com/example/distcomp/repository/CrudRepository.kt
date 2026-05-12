package com.example.distcomp.repository

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface CrudRepository<T> {
    fun save(entity: T): T
    fun findById(id: Long): T?
    fun findAll(): List<T>
    fun findAll(pageable: Pageable): Page<T>
    fun deleteById(id: Long): Boolean
    fun existsById(id: Long): Boolean
}
