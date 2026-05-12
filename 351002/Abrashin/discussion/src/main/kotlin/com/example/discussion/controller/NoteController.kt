package com.example.discussion.controller

import com.example.discussion.dto.request.NoteRequestTo
import com.example.discussion.dto.response.NoteResponseTo
import com.example.discussion.service.NoteReplyProducer
import com.example.discussion.service.NoteService
import com.example.notecontracts.NoteOperation
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1.0/notes")
class NoteController(
    private val service: NoteService,
    private val replyProducer: NoteReplyProducer
) {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@Valid @RequestBody request: NoteRequestTo): NoteResponseTo {
        val note = service.create(request)
        replyProducer.sendSync(NoteOperation.CREATE, note, HttpStatus.CREATED.value())
        return service.toResponse(note)
    }

    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long): NoteResponseTo = service.getById(id)

    @GetMapping
    fun getAll(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @RequestParam(defaultValue = "id,desc") sort: Array<String>
    ): List<NoteResponseTo> = service.getAll(page, size, sort)

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @Valid @RequestBody request: NoteRequestTo): NoteResponseTo {
        val note = service.put(id, request)
        replyProducer.sendSync(NoteOperation.PUT, note, HttpStatus.OK.value())
        return service.toResponse(note)
    }

    @PatchMapping("/{id}")
    fun patch(@PathVariable id: Long, @Valid @RequestBody request: NoteRequestTo): NoteResponseTo {
        val note = service.patch(id, request)
        replyProducer.sendSync(NoteOperation.PATCH, note, HttpStatus.OK.value())
        return service.toResponse(note)
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(@PathVariable id: Long) {
        val note = service.delete(id)
        replyProducer.sendSync(NoteOperation.DELETE, note, HttpStatus.NO_CONTENT.value())
    }
}
