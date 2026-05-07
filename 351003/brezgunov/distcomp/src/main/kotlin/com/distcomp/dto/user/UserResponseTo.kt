package com.distcomp.dto.user

import java.io.Serializable

data class UserResponseTo (
    val id: Long,
    val login: String,
    val firstname: String,
    val lastname: String,
) : Serializable