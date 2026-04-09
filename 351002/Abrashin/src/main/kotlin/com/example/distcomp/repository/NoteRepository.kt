package com.example.distcomp.repository

import com.example.distcomp.model.Note

interface NoteRepository : CrudRepository<Note> {
    fun findByTweetId(tweetId: Long): List<Note>
}
