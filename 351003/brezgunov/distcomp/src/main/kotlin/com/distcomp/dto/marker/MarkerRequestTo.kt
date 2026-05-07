package com.distcomp.dto.marker

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class MarkerRequestTo(
    val id: Long? = null,
    @field:NotBlank
    @field:Size(min = 3, max = 32)
    val name: String,
)
