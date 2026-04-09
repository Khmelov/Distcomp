package com.example.distcomp.data.repository

import com.example.distcomp.model.BaseEntity
import com.example.distcomp.data.datasource.BaseDataSource
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification

import com.example.distcomp.repository.CrudRepository

abstract class DataSourceRepository<T : BaseEntity, D>(
    private val dataSource: BaseDataSource<T, D>
) : CrudRepository<T> {
    override fun save(entity: T): T = dataSource.save(entity)
    override fun findById(id: Long): T? = dataSource.findById(id)
    override fun findAll(): List<T> = dataSource.findAll()
    override fun findAll(pageable: Pageable): Page<T> = dataSource.findAll(pageable)
    fun findAll(spec: Specification<D>?, pageable: Pageable): Page<T> =
        dataSource.findAll(spec, pageable)
    override fun deleteById(id: Long): Boolean = dataSource.deleteById(id)
    override fun existsById(id: Long): Boolean = dataSource.existsById(id)
}
