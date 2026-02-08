package com.distcomp.dto.user

data class UserRequestTo(
    var username: String? = null,
    var password: String,
    var firstname: String,
    var lastname: String,
)
