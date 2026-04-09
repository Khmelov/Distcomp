package com.example.distcomp.model

data class Note(
    var tweetId: Long? = null,
    var content: String? = null
) : BaseEntity()
