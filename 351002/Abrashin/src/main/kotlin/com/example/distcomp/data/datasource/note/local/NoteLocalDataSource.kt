package com.example.distcomp.data.datasource.note.local

import com.example.distcomp.data.datasource.note.NoteDataSource
import com.example.distcomp.data.dbo.NoteDbo
import com.example.distcomp.data.mapper.NoteDboMapper
import com.example.distcomp.model.Note
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import com.example.distcomp.data.datasource.tweet.local.TweetJpaRepository
import org.springframework.stereotype.Component

@Component
class NoteLocalDataSource(
    private val repository: NoteJpaRepository,
    private val tweetRepository: TweetJpaRepository,
    private val mapper: NoteDboMapper
) : NoteDataSource {
    override fun save(entity: Note): Note {
        val dbo = mapper.toDbo(entity)
        entity.tweetId?.let {
            dbo.tweet = tweetRepository.getReferenceById(it)
        }
        return mapper.toModel(repository.save(dbo))
    }

    override fun findById(id: Long): Note? = repository.findById(id).map { mapper.toModel(it) }.orElse(null)

    override fun findAll(): List<Note> = repository.findAll().map { mapper.toModel(it) }

    override fun findAll(pageable: Pageable): Page<Note> = repository.findAll(pageable).map { mapper.toModel(it) }

    override fun findAll(spec: Specification<NoteDbo>?, pageable: Pageable): Page<Note> =
        repository.findAll(spec, pageable).map { mapper.toModel(it) }

    override fun deleteById(id: Long): Boolean {
        return if (repository.existsById(id)) {
            repository.deleteById(id)
            true
        } else false
    }

    override fun existsById(id: Long): Boolean = repository.existsById(id)

    override fun findByTweetId(tweetId: Long): List<Note> = repository.findByTweetId(tweetId).map { mapper.toModel(it) }
}
