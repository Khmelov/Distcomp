package com.example.discussion.entity

import org.springframework.data.cassandra.core.mapping.Column
import org.springframework.data.cassandra.core.mapping.PrimaryKey
import org.springframework.data.cassandra.core.mapping.Table

@Table("tbl_notice")
data class Notice(
    @PrimaryKey
    var id: Long = 0,

    @Column("news_id")
    var newsId: Long = 0,

    @Column("content")
    var content: String = ""
)