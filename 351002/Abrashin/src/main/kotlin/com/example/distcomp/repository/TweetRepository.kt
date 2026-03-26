package com.example.distcomp.repository

import com.example.distcomp.model.Tweet
import org.springframework.stereotype.Repository

@Repository
class TweetRepository : InMemoryRepository<Tweet>() {
    fun findByTitle(title: String) = storage.values.find { it.title == title }
}
