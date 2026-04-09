package com.example.distcomp.data.datasource.tweet.local

import com.example.distcomp.data.datasource.tweet.TweetDataSource
import com.example.distcomp.data.dbo.TweetDbo
import com.example.distcomp.data.mapper.TweetDboMapper
import com.example.distcomp.model.Tweet
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import com.example.distcomp.data.datasource.creator.local.CreatorJpaRepository
import org.springframework.stereotype.Component

@Component
class TweetLocalDataSource(
    private val repository: TweetJpaRepository,
    private val creatorRepository: CreatorJpaRepository,
    private val mapper: TweetDboMapper
) : TweetDataSource {
    override fun save(entity: Tweet): Tweet {
        val dbo = mapper.toDbo(entity)
        entity.creatorId?.let { 
            dbo.creator = creatorRepository.getReferenceById(it) 
        }
        return mapper.toModel(repository.save(dbo))
    }

    override fun findById(id: Long): Tweet? = repository.findById(id).map { mapper.toModel(it) }.orElse(null)

    override fun findAll(): List<Tweet> = repository.findAll().map { mapper.toModel(it) }

    override fun findAll(pageable: Pageable): Page<Tweet> = repository.findAll(pageable).map { mapper.toModel(it) }

    override fun findAll(spec: Specification<TweetDbo>?, pageable: Pageable): Page<Tweet> =
        repository.findAll(spec, pageable).map { mapper.toModel(it) }

    override fun deleteById(id: Long): Boolean {
        return if (repository.existsById(id)) {
            repository.deleteById(id)
            true
        } else false
    }

    override fun existsById(id: Long): Boolean = repository.existsById(id)

    override fun existsByCreatorIdAndTitle(creatorId: Long, title: String): Boolean =
        repository.existsByCreatorIdAndTitle(creatorId, title)
}
