package com.example.distcomp.data.repository

import com.example.distcomp.data.datasource.note.NoteDataSource
import com.example.distcomp.data.dbo.NoteDbo
import com.example.distcomp.model.Note
import org.springframework.stereotype.Repository

import com.example.distcomp.repository.NoteRepository

@Repository
class NoteRepositoryImpl(
    private val dataSource: NoteDataSource
) : DataSourceRepository<Note, NoteDbo>(dataSource), NoteRepository {
    override fun findByTweetId(tweetId: Long): List<Note> = dataSource.findByTweetId(tweetId)
}
