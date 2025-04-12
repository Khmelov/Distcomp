package bsuir.dc.discussion.controller

import bsuir.dc.discussion.dto.from.PostRequestTo
import bsuir.dc.discussion.dto.to.PostResponseTo
import bsuir.dc.discussion.service.PostService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import jakarta.validation.Valid

@RestController
@RequestMapping("/api/v1.0/posts")
class PostController(
    private val postService: PostService
) {
    @PostMapping
    fun createPost(
        @RequestBody @Valid postRequestTo: PostRequestTo,
        @RequestHeader("X-Country", defaultValue = "Belarus") country: String
    ): ResponseEntity<PostResponseTo> {
        val createdPost = postService.createPost(postRequestTo, country)
        println(createdPost)
        return ResponseEntity(createdPost, HttpStatus.CREATED)
    }

    @GetMapping
    fun getAllPosts(): ResponseEntity<List<PostResponseTo>> {
        val posts = postService.getAllPosts()
        return ResponseEntity.ok(posts)
    }

    @GetMapping("/{id}")
    fun getPostById(
        @PathVariable id: Long,
        @RequestHeader("X-Country", defaultValue = "Belarus") country: String
    ): ResponseEntity<PostResponseTo> {
        val post = postService.getPostById(country, id)
        return ResponseEntity.ok(post)
    }

    @DeleteMapping("/{id}")
    fun deletePost(
        @PathVariable id: Long,
        @RequestHeader("X-Country", defaultValue = "Belarus") country: String
    ): ResponseEntity<Void> {
        postService.deletePost(country, id)
        return ResponseEntity.noContent().build()
    }

    @PutMapping()
    fun updatePost(
        @RequestBody @Valid postRequestTo: PostRequestTo,
        @RequestHeader("X-Country", defaultValue = "Belarus") country: String
    ): ResponseEntity<PostResponseTo> {
        val updatedPost = postService.updatePost(country, postRequestTo)
        return ResponseEntity.ok(updatedPost)
    }
}
