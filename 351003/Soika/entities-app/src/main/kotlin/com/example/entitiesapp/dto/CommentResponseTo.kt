package com.example.entitiesapp.dto

data class CommentResponseTo(
    val id: Long,
    val content: String,
    val storyId: Long
)