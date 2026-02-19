package com.distcomp.service

import com.distcomp.dto.news.NewsRequestTo
import com.distcomp.dto.news.NewsResponseTo
import com.distcomp.exception.NewsNotFoundException
import com.distcomp.exception.ValidationException
import com.distcomp.mapper.NewsMapper
import com.distcomp.repository.NewsRepository
import com.distcomp.repository.UserRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class NewsService(
    val newsMapper: NewsMapper,
    val newsRepository: NewsRepository,
    val userRepository: UserRepository
) {
    fun createNews(newsRequestTo: NewsRequestTo): NewsResponseTo {
        val news = newsMapper.toNewsEntity(newsRequestTo)
        val user = userRepository.findByIdOrNull(newsRequestTo.userId)
        news.user = user
        newsRepository.save(news)
        return newsMapper.toNewsResponse(news)
    }

    fun readNewsById(id: Long): NewsResponseTo {
        val news = newsRepository.findByIdOrNull(id) ?: throw NewsNotFoundException("User not found")
        return newsMapper.toNewsResponse(news)
    }

    fun readAll(): List<NewsResponseTo> {
        return newsRepository.findAll().map { newsMapper.toNewsResponse(it) }
    }

    fun updateNews(newsRequestTo: NewsRequestTo, newsId: Long?): NewsResponseTo {
        if (newsId == null || newsRepository.findByIdOrNull(newsId) == null) {
            throw NewsNotFoundException("User not found")
        }

        if (newsRequestTo.title.length < 2) {
            throw ValidationException("New title is too short")
        }

        val news = newsMapper.toNewsEntity(newsRequestTo)
        news.id = newsId
        newsRepository.save(news)
        val user = userRepository.findByIdOrNull(newsRequestTo.userId)
        news.user = user
        return newsMapper.toNewsResponse(news)
    }

    fun removeNewsById(id: Long) {
        if (newsRepository.findByIdOrNull(id) == null) {
            throw NewsNotFoundException("News not found")
        }

        newsRepository.deleteById(id)
    }
}