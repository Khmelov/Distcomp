package com.example.entitiesapp.model

data class Mark(
    override var id: Long? = null,
    var name: String
) : BaseEntity(id)