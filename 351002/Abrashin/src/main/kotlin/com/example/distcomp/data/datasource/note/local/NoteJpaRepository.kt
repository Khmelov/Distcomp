package com.example.distcomp.data.datasource.note.local

import com.example.distcomp.data.dbo.NoteDbo
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor

interface NoteJpaRepository : JpaRepository<NoteDbo, Long>, JpaSpecificationExecutor<NoteDbo> {
    fun findByTweetId(tweetId: Long): List<NoteDbo>
}
