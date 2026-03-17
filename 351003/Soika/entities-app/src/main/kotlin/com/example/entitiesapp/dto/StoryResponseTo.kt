package com.example.entitiesapp.dto

data class StoryResponseTo(
    val id: Long,
    val title: String,
    val content: String,
    val writerId: Long,
    val markIds: Set<Long>
)