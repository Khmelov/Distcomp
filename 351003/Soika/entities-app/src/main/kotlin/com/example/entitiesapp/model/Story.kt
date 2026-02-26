package com.example.entitiesapp.model

data class Story(
    override var id: Long? = null,
    var title: String,
    var content: String,
    var writerId: Long,
    var markIds: MutableSet<Long> = mutableSetOf()
) : BaseEntity(id)