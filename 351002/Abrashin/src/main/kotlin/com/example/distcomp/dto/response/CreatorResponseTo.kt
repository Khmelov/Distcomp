package com.example.distcomp.dto.response

import com.fasterxml.jackson.annotation.JsonRootName

@JsonRootName("creator")
data class CreatorResponseTo(
    var id: Long? = null,
    var login: String? = null,
    var firstname: String? = null,
    var lastname: String? = null
)
