package com.example.distcomp.controller

import com.example.distcomp.dto.request.StickerRequestTo
import com.example.distcomp.dto.response.StickerResponseTo
import com.example.distcomp.service.StickerService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1.0/stickers")
class StickerController(private val service: StickerService) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@Valid @RequestBody request: StickerRequestTo): StickerResponseTo {
        return service.create(request)
    }

    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long): StickerResponseTo {
        return service.getById(id)
    }

    @GetMapping
    fun getAll(): List<StickerResponseTo> {
        return service.getAll()
    }

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @Valid @RequestBody request: StickerRequestTo): StickerResponseTo {
        return service.patch(id, request)
    }

    @PatchMapping("/{id}")
    fun patch(@PathVariable id: Long, @Valid @RequestBody request: StickerRequestTo): StickerResponseTo {
        return service.patch(id, request)
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(@PathVariable id: Long) {
        service.delete(id)
    }
}
