package com.example.entitiesapp.model

import com.example.entitiesapp.dto.CommentState
import org.springframework.data.cassandra.core.cql.Ordering
import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.Column
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table

@Table("tbl_comment")
data class Comment(
    @PrimaryKeyColumn(name = "story_id", type = PrimaryKeyType.PARTITIONED, ordinal = 0)
    val storyId: Long,

    @PrimaryKeyColumn(name = "id", type = PrimaryKeyType.CLUSTERED, ordinal = 1, ordering = Ordering.DESCENDING)
    val id: Long,

    @Column("country")
    val country: String,

    @Column("content")
    val content: String,

    @Column("state")
    val state: CommentState = CommentState.PENDING
)