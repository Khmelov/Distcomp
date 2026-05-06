package com.example.distcomp.controller

import com.example.distcomp.dto.request.StickerRequestTo
import com.example.distcomp.dto.response.StickerResponseTo
import com.example.distcomp.security.AuthorizationService
import com.example.distcomp.service.StickerService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v2.0/stickers")
class StickerV2Controller(
    private val service: StickerService,
    private val authorizationService: AuthorizationService
) {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@Valid @RequestBody request: StickerRequestTo): StickerResponseTo {
        authorizationService.ensureAdmin()
        return service.create(request)
    }

    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long): StickerResponseTo = service.getById(id)

    @GetMapping
    fun getAll(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @RequestParam(defaultValue = "id,desc") sort: Array<String>
    ): List<StickerResponseTo> = service.getAll(page, size, sort)

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @Valid @RequestBody request: StickerRequestTo): StickerResponseTo {
        authorizationService.ensureAdmin()
        return service.patch(id, request)
    }

    @PatchMapping("/{id}")
    fun patch(@PathVariable id: Long, @Valid @RequestBody request: StickerRequestTo): StickerResponseTo {
        authorizationService.ensureAdmin()
        return service.patch(id, request)
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(@PathVariable id: Long) {
        authorizationService.ensureAdmin()
        service.delete(id)
    }
}
