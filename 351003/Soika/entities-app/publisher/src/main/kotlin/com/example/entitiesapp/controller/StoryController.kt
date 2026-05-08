package com.example.entitiesapp.controller

import com.example.entitiesapp.config.ApiConfig
import com.example.entitiesapp.dto.StoryRequestTo
import com.example.entitiesapp.dto.StoryResponseTo
import com.example.entitiesapp.exception.ValidationException
import com.example.entitiesapp.service.StoryService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("${ApiConfig.API_V1}/stories")
class StoryController(
    private val service: StoryService
) {

    @GetMapping
    fun getAll() = ResponseEntity.ok(service.getAll())

    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long) = ResponseEntity.ok(service.getById(id))

    @PostMapping
    fun create(@Valid @RequestBody dto: StoryRequestTo) =
        ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto))

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @Valid @RequestBody dto: StoryRequestTo) =
        ResponseEntity.ok(service.update(id, dto))

    @PutMapping
    fun updateFromBody(@Valid @RequestBody dto: StoryRequestTo): ResponseEntity<StoryResponseTo> {
        val id = dto.id ?: throw ValidationException("Id is required", 40000)
        return ResponseEntity.ok(service.update(id, dto))
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<Void> {
        service.delete(id)
        return ResponseEntity.noContent().build()
    }
}