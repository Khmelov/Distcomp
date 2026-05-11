package com.example.distcomp.data.datasource.creator.local

import com.example.distcomp.data.datasource.creator.CreatorDataSource
import com.example.distcomp.data.dbo.CreatorDbo
import com.example.distcomp.data.mapper.CreatorDboMapper
import com.example.distcomp.model.Creator
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Component

@Component
class CreatorLocalDataSource(
    private val repository: CreatorJpaRepository, private val mapper: CreatorDboMapper
) : CreatorDataSource {
    override fun save(entity: Creator): Creator = mapper.toModel(repository.save(mapper.toDbo(entity)))

    override fun findById(id: Long): Creator? = repository.findById(id).map { mapper.toModel(it) }.orElse(null)

    override fun findAll(): List<Creator> = repository.findAll().map { mapper.toModel(it) }

    override fun findAll(pageable: Pageable): Page<Creator> = repository.findAll(pageable).map { mapper.toModel(it) }

    override fun findAll(spec: Specification<CreatorDbo>?, pageable: Pageable): Page<Creator> =
        repository.findAll(spec, pageable).map { mapper.toModel(it) }

    override fun deleteById(id: Long): Boolean {
        return if (repository.existsById(id)) {
            repository.deleteById(id)
            true
        } else false
    }

    override fun existsById(id: Long): Boolean = repository.existsById(id)

    override fun findByLogin(login: String): Creator? =
        repository.findByLogin(login).map { mapper.toModel(it) }.orElse(null)
}
