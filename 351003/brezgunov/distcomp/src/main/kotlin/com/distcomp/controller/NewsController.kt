package com.distcomp.controller

import com.distcomp.dto.news.NewsRequestTo
import com.distcomp.dto.news.NewsResponseTo
import com.distcomp.service.NewsService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/{version}/news")
class NewsController (
    private val newsService: NewsService
) {
    @PostMapping(version = "1.0")
    @ResponseStatus(HttpStatus.CREATED)
    fun createUser(@RequestBody newsRequestTo: NewsRequestTo): NewsResponseTo {
        return newsService.createNews(newsRequestTo)
    }

    @GetMapping("{id}")
    fun readUserById(@PathVariable("id") id: Long): NewsResponseTo {
        return newsService.readNewsById(id)
    }

    @GetMapping
    fun findAll(): List<NewsResponseTo> {
        return newsService.readAll()
    }

    @PutMapping(path = ["/{id}", ""], version = "1.0")
    @ResponseStatus(HttpStatus.OK)
    fun updateUser(@RequestBody newsRequestTo: NewsRequestTo,
                   @PathVariable("id") userId: Long?): NewsResponseTo {
        return newsService.updateNews(newsRequestTo, userId)
    }

    @DeleteMapping(path = ["/{id}"], version = "1.0")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteUser(@PathVariable("id") id: Long) {
        newsService.removeNewsById(id)
    }
}