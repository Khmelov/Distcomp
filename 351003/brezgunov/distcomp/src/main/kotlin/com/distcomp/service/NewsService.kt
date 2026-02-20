package com.distcomp.service

import com.distcomp.dto.news.NewsRequestTo
import com.distcomp.dto.news.NewsResponseTo
import com.distcomp.entity.News
import com.distcomp.entity.User
import com.distcomp.exception.NewsNotFoundException
import com.distcomp.exception.ValidationException
import com.distcomp.mapper.NewsMapper
import com.distcomp.repository.CrudRepository
import org.springframework.stereotype.Service

@Service
class NewsService (
    val newsMapper: NewsMapper,
    val newsRepository: CrudRepository<News>,
    val userRepository: CrudRepository<User>
) {
    fun createNews(newsRequestTo: NewsRequestTo): NewsResponseTo {
        val news = newsMapper.toNewsEntity(newsRequestTo)
        newsRepository.save(news)
        val user = userRepository.findById(newsRequestTo.userId)
        news.user = user
        return newsMapper.toNewsResponse(news)
    }

    fun readNewsById(id: Long): NewsResponseTo {
        val user = newsRepository.findById(id) ?: throw NewsNotFoundException("User not found")
        return newsMapper.toNewsResponse(user)
    }

    fun readAll(): List<NewsResponseTo> {
        return newsRepository.findAll().map { newsMapper.toNewsResponse(it) }
    }

    fun updateNews(newsRequestTo: NewsRequestTo, newsId: Long?): NewsResponseTo {
        if (newsId == null || newsRepository.findById(newsId) == null) {
            throw NewsNotFoundException("User not found")
        }

        if (newsRequestTo.title.length < 2) {
            throw ValidationException("New title is too short")
        }

        val news = newsMapper.toNewsEntity(newsRequestTo)
        news.id = newsId
        newsRepository.save(news)
        val user = userRepository.findById(newsRequestTo.userId)
        news.user = user
        return newsMapper.toNewsResponse(news)
    }

    fun removeNewsById(id: Long) {
        if (newsRepository.findById(id) == null) {
            throw NewsNotFoundException("News not found")
        }

        newsRepository.removeById(id)
    }
}