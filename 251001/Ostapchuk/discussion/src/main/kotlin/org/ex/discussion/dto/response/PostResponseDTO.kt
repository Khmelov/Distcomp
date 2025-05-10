package org.ex.discussion.dto.response

data class PostResponseDTO (
    val id: Long,
    val country: String,
    val tweetId: Long,
    val content: String
)