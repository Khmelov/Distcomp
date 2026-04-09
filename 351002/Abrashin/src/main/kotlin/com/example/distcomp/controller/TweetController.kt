package com.example.distcomp.controller

import com.example.distcomp.dto.response.CreatorResponseTo
import com.example.distcomp.dto.response.NoteResponseTo
import com.example.distcomp.dto.response.StickerResponseTo
import com.example.distcomp.dto.request.TweetRequestTo
import com.example.distcomp.dto.response.TweetResponseTo
import com.example.distcomp.service.TweetService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1.0/tweets")
class TweetController(private val service: TweetService) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@Valid @RequestBody request: TweetRequestTo): TweetResponseTo {
        return service.create(request)
    }

    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long): TweetResponseTo {
        return service.getById(id)
    }

    @GetMapping
    fun getAll(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @RequestParam(defaultValue = "id,desc") sort: Array<String>
    ): List<TweetResponseTo> {
        return service.getAll(page, size, sort)
    }

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @Valid @RequestBody request: TweetRequestTo): TweetResponseTo {
        return service.patch(id, request)
    }

    @PatchMapping("/{id}")
    fun patch(@PathVariable id: Long, @Valid @RequestBody request: TweetRequestTo): TweetResponseTo {
        return service.patch(id, request)
    }

    @GetMapping("/{id}/creator")
    fun getCreatorByTweetId(@PathVariable id: Long): CreatorResponseTo {
        return service.getCreatorByTweetId(id)
    }

    @GetMapping("/{id}/stickers")
    fun getStickersByTweetId(@PathVariable id: Long): List<StickerResponseTo> {
        return service.getStickersByTweetId(id)
    }

    @GetMapping("/{id}/notes")
    fun getNotesByTweetId(@PathVariable id: Long): List<NoteResponseTo> {
        return service.getNotesByTweetId(id)
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(@PathVariable id: Long) {
        service.delete(id)
    }
}
