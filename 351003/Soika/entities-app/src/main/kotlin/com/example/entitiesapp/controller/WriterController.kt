package com.example.entitiesapp.controller

import com.example.entitiesapp.config.ApiConfig
import com.example.entitiesapp.dto.WriterRequestTo
import com.example.entitiesapp.dto.WriterResponseTo
import com.example.entitiesapp.service.WriterService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("${ApiConfig.API_V1}/writers")
class WriterController(
    private val service: WriterService
) {

    @GetMapping
    fun getAll(): ResponseEntity<List<WriterResponseTo>> =
        ResponseEntity.ok(service.getAll())

    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long): ResponseEntity<WriterResponseTo> =
        ResponseEntity.ok(service.getById(id))

    @PostMapping
    fun create(@Valid @RequestBody dto: WriterRequestTo): ResponseEntity<WriterResponseTo> =
        ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto))

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @Valid @RequestBody dto: WriterRequestTo): ResponseEntity<WriterResponseTo> =
        ResponseEntity.ok(service.update(id, dto))

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<Void> {
        service.delete(id)
        return ResponseEntity.noContent().build()
    }
}