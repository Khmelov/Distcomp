package com.example.distcomp.repository

import com.example.distcomp.model.Tweet

interface TweetRepository : CrudRepository<Tweet> {
    fun existsByCreatorIdAndTitle(creatorId: Long, title: String): Boolean
}
