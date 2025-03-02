package bsuir.dc.rest.entity

data class Post(
    override var id: Long = 0L,
    val issueId: Long,
    val content: String
) : Identifiable
