package com.example.distcomp.data.datasource.tweet.local

import com.example.distcomp.data.dbo.TweetDbo
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor

interface TweetJpaRepository : JpaRepository<TweetDbo, Long>, JpaSpecificationExecutor<TweetDbo> {
    fun existsByCreatorIdAndTitle(creatorId: Long, title: String): Boolean
}
