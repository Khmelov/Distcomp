package com.example.entitiesapp.model

data class Comment(
    override var id: Long? = null,
    var content: String,
    var storyId: Long
) : BaseEntity(id)