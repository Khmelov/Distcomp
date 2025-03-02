package bsuir.dc.rest.entity

import java.time.LocalDateTime

data class Issue(
    override var id: Long = 0L,
    val writerId: Long,
    val title: String,
    val content: String,
    val created: LocalDateTime = LocalDateTime.now(),
    var modified: LocalDateTime = LocalDateTime.now(),
) : Identifiable
