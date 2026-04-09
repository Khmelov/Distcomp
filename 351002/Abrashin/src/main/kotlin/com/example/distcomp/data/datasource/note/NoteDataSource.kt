package com.example.distcomp.data.datasource.note

import com.example.distcomp.data.datasource.BaseDataSource
import com.example.distcomp.data.dbo.NoteDbo
import com.example.distcomp.model.Note

interface NoteDataSource : BaseDataSource<Note, NoteDbo> {
    fun findByTweetId(tweetId: Long): List<Note>
}
