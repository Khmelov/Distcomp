package com.example.entitiesapp.dto

import java.io.Serializable

data class WriterResponseTo(
    val id: Long = 0,
    val login: String = "",
    val password: String = "",
    val firstname: String = "",
    val lastname: String = "",
    val role: Role = Role.CUSTOMER
) : Serializable