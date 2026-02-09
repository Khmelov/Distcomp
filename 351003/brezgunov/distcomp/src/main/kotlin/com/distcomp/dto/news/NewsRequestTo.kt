package com.distcomp.dto.news

data class NewsRequestTo (
    val id: Long? = null,
    val title: String,
    val content: String,
    val userId: Long
)