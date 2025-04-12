package bsuir.dc.discussion.entity

import jakarta.validation.constraints.Size
import org.springframework.data.cassandra.core.mapping.PrimaryKey
import org.springframework.data.cassandra.core.mapping.Table

@Table("tbl_post")
data class Post(
    @PrimaryKey
    val key: PostKey,

    val issueId: Long,

    @field:Size(min = 2, max = 2048)
    val content: String
)
