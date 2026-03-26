package com.example.entitiesapp.controller

import com.example.entitiesapp.config.ApiConfig
import com.example.entitiesapp.dto.MarkRequestTo
import com.example.entitiesapp.service.MarkService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("${ApiConfig.API_V1}/marks")
class MarkController(
    private val service: MarkService
) {

    @GetMapping
    fun getAll() = ResponseEntity.ok(service.getAll())

    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long) = ResponseEntity.ok(service.getById(id))

    @PostMapping
    fun create(@RequestBody dto: MarkRequestTo) =
        ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto))

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody dto: MarkRequestTo) =
        ResponseEntity.ok(service.update(id, dto))

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<Void> {
        service.delete(id)
        return ResponseEntity.noContent().build()
    }
}