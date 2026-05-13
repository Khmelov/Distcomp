package com.example.entitiesapp.controller

import com.example.entitiesapp.config.ApiConfig
import com.example.entitiesapp.dto.CommentRequestTo
import com.example.entitiesapp.service.CommentService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("${ApiConfig.API_V1}/comments")
class CommentController(private val service: CommentService) {

    @GetMapping
    fun getAll() = service.getAll()

    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long) = service.getById(id)

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@Valid @RequestBody dto: CommentRequestTo) = service.create(dto)

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @Valid @RequestBody dto: CommentRequestTo) = service.update(id, dto)

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(@PathVariable id: Long) = service.delete(id)

    // Специальный эндпоинт для внутреннего использования Publisher-ом
    @DeleteMapping("/story/{storyId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteByStory(@PathVariable storyId: Long) = service.deleteByStoryId(storyId)
}