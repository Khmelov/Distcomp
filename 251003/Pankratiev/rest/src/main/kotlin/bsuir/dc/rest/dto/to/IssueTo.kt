package bsuir.dc.rest.dto.to

import java.time.LocalDateTime

data class IssueTo(
    val id: Long,
    val writerId: Long,
    val title: String,
    val content: String,
    val created: LocalDateTime,
    val modified: LocalDateTime
)
