package com.example.entitiesapp.model

data class Writer(
    override var id: Long? = null,
    var login: String,
    var password: String,
    var firstname: String,
    var lastname: String
) : BaseEntity(id)