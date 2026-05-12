package com.example.distcomp.controller

import com.example.distcomp.dto.request.CreatorRequestTo
import com.example.distcomp.dto.response.CreatorResponseTo
import com.example.distcomp.security.AuthorizationService
import com.example.distcomp.service.CreatorService
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
@RequestMapping("/api/v2.0/creators")
class CreatorV2Controller(
    private val service: CreatorService,
    private val authorizationService: AuthorizationService
) {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@Valid @RequestBody request: CreatorRequestTo): CreatorResponseTo =
        service.create(request)

    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long): CreatorResponseTo = service.getById(id)

    @GetMapping
    fun getAll(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @RequestParam(defaultValue = "id,desc") sort: Array<String>
    ): List<CreatorResponseTo> = service.getAll(page, size, sort)

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @Valid @RequestBody request: CreatorRequestTo): CreatorResponseTo {
        authorizationService.ensureAdminOrCurrentCreator(id)
        authorizationService.ensureCreatorRoleChangeAllowed(request)
        return service.patch(id, request)
    }

    @PatchMapping("/{id}")
    fun patch(@PathVariable id: Long, @Valid @RequestBody request: CreatorRequestTo): CreatorResponseTo {
        authorizationService.ensureAdminOrCurrentCreator(id)
        authorizationService.ensureCreatorRoleChangeAllowed(request)
        return service.patch(id, request)
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(@PathVariable id: Long) {
        authorizationService.ensureAdminOrCurrentCreator(id)
        service.delete(id)
    }
}
