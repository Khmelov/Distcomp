package com.distcomp.dto.marker

import java.io.Serializable

data class MarkerResponseTo(
    val id: Long,
    val name: String,
    val newsId: Long
) : Serializable
