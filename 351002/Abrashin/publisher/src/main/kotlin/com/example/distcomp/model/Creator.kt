package com.example.distcomp.model

data class Creator(
    var login: String? = null,
    var password: String? = null,
    var firstname: String? = null,
    var lastname: String? = null
) : BaseEntity()
