package com.distcomp.entity

class User (
    var id: Long? = null,
    var login: String,
    var password: String,
    var firstname: String,
    var lastname: String,
    var news: List<News>?
)