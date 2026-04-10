package com.example.distcomp.data.dbo

import com.example.notecontracts.NoteState
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "tbl_note")
class NoteProjectionDbo(
    @Id
    var id: Long = 0,
    @Column(name = "tweet_id", nullable = false)
    var tweetId: Long = 0,
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var state: NoteState = NoteState.PENDING,
    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()
)
