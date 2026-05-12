package com.example.distcomp.data.datasource.tweet

import com.example.distcomp.data.datasource.BaseDataSource
import com.example.distcomp.data.dbo.TweetDbo
import com.example.distcomp.model.Tweet

interface TweetDataSource : BaseDataSource<Tweet, TweetDbo> {
    fun existsByCreatorIdAndTitle(creatorId: Long, title: String): Boolean
}
