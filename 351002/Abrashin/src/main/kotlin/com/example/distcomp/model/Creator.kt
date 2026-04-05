package com.example.distcomp.model

class Creator(
    id: Long? = null,
    var login: String = "",
    var password: String = "",
    var firstname: String = "",
    var lastname: String = ""
) : BaseEntity(id)
