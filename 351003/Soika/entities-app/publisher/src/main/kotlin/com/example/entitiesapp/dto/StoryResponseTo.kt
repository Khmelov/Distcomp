package com.example.entitiesapp.dto

import java.io.Serializable

data class StoryResponseTo(
    val id: Long = 0,
    val title: String = "",
    val content: String = "",
    val writerId: Long = 0,
    val markIds: Set<Long> = emptySet()
) : Serializable