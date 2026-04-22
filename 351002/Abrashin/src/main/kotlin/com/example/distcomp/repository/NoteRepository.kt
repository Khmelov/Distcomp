package com.example.distcomp.repository

import com.example.distcomp.model.Note
import org.springframework.stereotype.Repository

@Repository
class NoteRepository : InMemoryRepository<Note>() {
    fun findByTweetId(tweetId: Long): List<Note> = storage.values.filter { it.tweetId == tweetId }
}
