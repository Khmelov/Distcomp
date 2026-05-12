package com.example.discussion.model

import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import java.io.Serializable

@PrimaryKeyClass
data class NoteKey(
    @PrimaryKeyColumn(name = "tweet_id", type = PrimaryKeyType.PARTITIONED)
    var tweetId: Long = 0,
    @PrimaryKeyColumn(name = "id", ordinal = 0, type = PrimaryKeyType.CLUSTERED)
    var id: Long = 0
) : Serializable
