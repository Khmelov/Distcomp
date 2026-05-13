package com.example.distcomp.controller

import com.example.distcomp.dto.request.TweetRequestTo
import com.example.distcomp.dto.response.CreatorResponseTo
import com.example.distcomp.dto.response.NoteResponseTo
import com.example.distcomp.dto.response.StickerResponseTo
import com.example.distcomp.dto.response.TweetResponseTo
import com.example.distcomp.security.AuthorizationService
import com.example.distcomp.service.TweetService
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
@RequestMapping("/api/v2.0/tweets")
class TweetV2Controller(
    private val service: TweetService,
    private val authorizationService: AuthorizationService
) {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@Valid @RequestBody request: TweetRequestTo): TweetResponseTo {
        authorizationService.ensureCanManageTweetRequest(request)
        return service.create(request)
    }

    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long): TweetResponseTo = service.getById(id)

    @GetMapping
    fun getAll(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @RequestParam(defaultValue = "id,desc") sort: Array<String>
    ): List<TweetResponseTo> = service.getAll(page, size, sort)

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @Valid @RequestBody request: TweetRequestTo): TweetResponseTo {
        authorizationService.ensureCanManageTweet(id, request.creatorId)
        return service.patch(id, request)
    }

    @PatchMapping("/{id}")
    fun patch(@PathVariable id: Long, @Valid @RequestBody request: TweetRequestTo): TweetResponseTo {
        authorizationService.ensureCanManageTweet(id, request.creatorId)
        return service.patch(id, request)
    }

    @GetMapping("/{id}/creator")
    fun getCreatorByTweetId(@PathVariable id: Long): CreatorResponseTo = service.getCreatorByTweetId(id)

    @GetMapping("/{id}/stickers")
    fun getStickersByTweetId(@PathVariable id: Long): List<StickerResponseTo> = service.getStickersByTweetId(id)

    @GetMapping("/{id}/notes")
    fun getNotesByTweetId(@PathVariable id: Long): List<NoteResponseTo> = service.getNotesByTweetId(id)

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(@PathVariable id: Long) {
        authorizationService.ensureCanManageTweet(id)
        service.delete(id)
    }
}
