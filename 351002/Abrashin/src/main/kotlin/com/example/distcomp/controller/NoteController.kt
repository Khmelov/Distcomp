package com.example.distcomp.controller

import com.example.distcomp.dto.request.NoteRequestTo
import com.example.distcomp.dto.response.NoteResponseTo
import com.example.distcomp.service.NoteService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1.0/notes")
class NoteController(private val service: NoteService) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@Valid @RequestBody request: NoteRequestTo): NoteResponseTo {
        return service.create(request)
    }

    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long): NoteResponseTo {
        return service.getById(id)
    }

    @GetMapping
    fun getAll(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @RequestParam(defaultValue = "id,desc") sort: Array<String>
    ): List<NoteResponseTo> {
        return service.getAll(page, size, sort)
    }

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @Valid @RequestBody request: NoteRequestTo): NoteResponseTo {
        return service.patch(id, request)
    }

    @PatchMapping("/{id}")
    fun patch(@PathVariable id: Long, @Valid @RequestBody request: NoteRequestTo): NoteResponseTo {
        return service.patch(id, request)
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(@PathVariable id: Long) {
        service.delete(id)
    }
}
