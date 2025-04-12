package bsuir.dc.publisher.dto.to

import java.time.LocalDateTime

data class IssueResponseTo(
    val id: Long,
    val writerId: Long,
    val title: String,
    val content: String,
    val created: LocalDateTime,
    val modified: LocalDateTime
)
