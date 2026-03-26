package com.example.entitiesapp.controller

import com.example.entitiesapp.config.ApiConfig
import com.example.entitiesapp.dto.CommentRequestTo
import com.example.entitiesapp.service.CommentService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("${ApiConfig.API_V1}/comments")
class CommentController(
    private val service: CommentService
) {

    @GetMapping
    fun getAll() = ResponseEntity.ok(service.getAll())

    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long) = ResponseEntity.ok(service.getById(id))

    @PostMapping
    fun create(@RequestBody dto: CommentRequestTo) =
        ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto))

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody dto: CommentRequestTo) =
        ResponseEntity.ok(service.update(id, dto))

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<Void> {
        service.delete(id)
        return ResponseEntity.noContent().build()
    }
}