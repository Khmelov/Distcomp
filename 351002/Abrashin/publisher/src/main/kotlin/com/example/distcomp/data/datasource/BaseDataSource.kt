package com.example.distcomp.data.datasource

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification

interface BaseDataSource<T, D> {
    fun save(entity: T): T
    fun findById(id: Long): T?
    fun findAll(): List<T>
    fun findAll(pageable: Pageable): Page<T>
    fun findAll(spec: Specification<D>?, pageable: Pageable): Page<T>
    fun deleteById(id: Long): Boolean
    fun existsById(id: Long): Boolean
}
