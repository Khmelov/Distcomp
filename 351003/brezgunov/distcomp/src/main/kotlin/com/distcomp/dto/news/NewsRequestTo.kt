package com.distcomp.dto.news

data class NewsRequestTo (
    var id: Long? = null,
    var title: String,
    var content: String,
    var userId: Long
)