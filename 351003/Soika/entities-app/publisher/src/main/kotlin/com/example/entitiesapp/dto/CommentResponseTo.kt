package com.example.entitiesapp.dto

import java.io.Serializable

data class CommentResponseTo(
    val id: Long = 0,
    val content: String = "",
    val storyId: Long = 0,
    val country: String = "",
    val state: CommentState = CommentState.PENDING
) : Serializable