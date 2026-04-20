package com.example.distcomp.model

class Note(
    id: Long? = null,
    var tweetId: Long? = null,
    var content: String = ""
) : BaseEntity(id)
