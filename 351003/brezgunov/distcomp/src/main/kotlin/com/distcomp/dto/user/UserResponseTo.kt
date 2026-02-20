package com.distcomp.dto.user

data class UserResponseTo (
    val id: Long,
    val login: String,
    val firstname: String,
    val lastname: String,
)