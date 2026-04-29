package com.distcomp.dto.user

import com.distcomp.entity.Role
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

data class UserRequestTo(
    val id: Long? = null,
    @field:NotBlank
    @field:Size(min = 2, max = 64)
    val login: String,
    @field:NotBlank
    @field:Size(min = 8, max = 128)
    val password: String,
    @field:NotBlank
    @field:Size(min = 2, max = 64)
    val firstname: String,
    @field:NotBlank
    @field:Size(min = 2, max = 64)
    val lastname: String,
    @field:NotNull
    var role: Role = Role.CUSTOMER
)
