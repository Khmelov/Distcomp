package com.example.distcomp.dto.request

import com.example.distcomp.model.CreatorRole
import com.fasterxml.jackson.annotation.JsonAlias
import com.fasterxml.jackson.annotation.JsonRootName
import jakarta.validation.constraints.Size

@JsonRootName("creator")
data class CreatorRequestTo(
    @field:Size(min = 2, max = 64)
    var login: String? = null,
    @field:Size(min = 8, max = 128)
    var password: String? = null,
    @field:JsonAlias("firstName")
    @field:Size(min = 2, max = 64)
    var firstname: String? = null,
    @field:JsonAlias("lastName")
    @field:Size(min = 2, max = 64)
    var lastname: String? = null,
    var role: CreatorRole? = null
)
