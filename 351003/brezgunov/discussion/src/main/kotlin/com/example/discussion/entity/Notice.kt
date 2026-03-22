package com.example.discussion.entity

import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table
import java.util.UUID

@Table("tbl_notice")
class Notice(
    @PrimaryKeyColumn(type = PrimaryKeyType.PARTITIONED)
    var country: String,

    @PrimaryKeyColumn(type = PrimaryKeyType.CLUSTERED)
    var newsId: Long,

    @PrimaryKeyColumn(type = PrimaryKeyType.CLUSTERED)
    var id: UUID,

    var content: String,
)