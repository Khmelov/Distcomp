package com.example.distcomp.data.datasource.sticker.local

import com.example.distcomp.data.datasource.sticker.StickerDataSource
import com.example.distcomp.data.dbo.StickerDbo
import com.example.distcomp.data.mapper.StickerDboMapper
import com.example.distcomp.model.Sticker
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Component

@Component
class StickerLocalDataSource(
    private val repository: StickerJpaRepository,
    private val mapper: StickerDboMapper
) : StickerDataSource {
    override fun save(entity: Sticker): Sticker = mapper.toModel(repository.save(mapper.toDbo(entity)))

    override fun findById(id: Long): Sticker? = repository.findById(id).map { mapper.toModel(it) }.orElse(null)

    override fun findAll(): List<Sticker> = repository.findAll().map { mapper.toModel(it) }

    override fun findAll(pageable: Pageable): Page<Sticker> = repository.findAll(pageable).map { mapper.toModel(it) }

    override fun findAll(spec: Specification<StickerDbo>?, pageable: Pageable): Page<Sticker> =
        repository.findAll(spec, pageable).map { mapper.toModel(it) }

    override fun deleteById(id: Long): Boolean {
        return if (repository.existsById(id)) {
            repository.deleteById(id)
            true
        } else false
    }

    override fun existsById(id: Long): Boolean = repository.existsById(id)

    override fun findByName(name: String): Sticker? = repository.findByName(name).map { mapper.toModel(it) }.orElse(null)
}
