package com.example.discussion.model

import org.springframework.data.cassandra.core.mapping.PrimaryKey
import org.springframework.data.cassandra.core.mapping.Table

@Table("tbl_note")
data class NoteEntity(
    @PrimaryKey
    var key: NoteKey = NoteKey(),
    var country: String? = null,
    var content: String = ""
)
