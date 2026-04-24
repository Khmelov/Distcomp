package com.example.entitiesapp.dto

import jakarta.validation.constraints.NotBlank

data class MarkRequestTo(
    @field:NotBlank
    val name: String
)