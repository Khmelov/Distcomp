package com.example.discussion.repository

import com.example.discussion.model.NoteEntity
import com.example.discussion.model.NoteKey
import org.springframework.data.cassandra.repository.AllowFiltering
import org.springframework.data.cassandra.repository.CassandraRepository

interface NoteRepository : CassandraRepository<NoteEntity, NoteKey> {
    fun findByKeyTweetId(tweetId: Long): List<NoteEntity>

    @AllowFiltering
    fun findByKeyId(id: Long): List<NoteEntity>
}
