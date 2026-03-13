package com.example.distcomp.controller

import com.example.distcomp.dto.request.CreatorRequestTo
import com.example.distcomp.dto.response.CreatorResponseTo
import com.example.distcomp.service.CreatorService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1.0/creators")
class CreatorController(private val service: CreatorService) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@Valid @RequestBody request: CreatorRequestTo): CreatorResponseTo {
        return service.create(request)
    }

    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long): CreatorResponseTo {
        return service.getById(id)
    }

    @GetMapping
    fun getAll(): List<CreatorResponseTo> {
        return service.getAll()
    }

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @Valid @RequestBody request: CreatorRequestTo): CreatorResponseTo {
        return service.patch(id, request)
    }

    @PatchMapping("/{id}")
    fun patch(@PathVariable id: Long, @Valid @RequestBody request: CreatorRequestTo): CreatorResponseTo {
        return service.patch(id, request)
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(@PathVariable id: Long) {
        service.delete(id)
    }
}
