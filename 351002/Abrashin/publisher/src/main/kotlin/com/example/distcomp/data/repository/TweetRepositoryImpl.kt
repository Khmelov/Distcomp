package com.example.distcomp.data.repository

import com.example.distcomp.data.datasource.tweet.TweetDataSource
import com.example.distcomp.data.dbo.TweetDbo
import com.example.distcomp.model.Tweet
import org.springframework.stereotype.Repository

import com.example.distcomp.repository.TweetRepository

@Repository
class TweetRepositoryImpl(
    private val dataSource: TweetDataSource
) : DataSourceRepository<Tweet, TweetDbo>(dataSource), TweetRepository {
    override fun existsByCreatorIdAndTitle(creatorId: Long, title: String): Boolean =
        dataSource.existsByCreatorIdAndTitle(creatorId, title)
}
