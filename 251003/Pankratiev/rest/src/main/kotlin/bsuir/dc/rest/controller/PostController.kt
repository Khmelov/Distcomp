package bsuir.dc.rest.controller

import bsuir.dc.rest.dto.from.PostFrom
import bsuir.dc.rest.dto.to.PostTo
import bsuir.dc.rest.service.PostService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1.0/posts")
class PostController(
    private val postService: PostService
) {

    @PostMapping
    fun createPost(@RequestBody @Valid postFrom: PostFrom): ResponseEntity<PostTo> {
        val createdPost = postService.createPost(postFrom)
        return ResponseEntity(createdPost, HttpStatus.CREATED)
    }

    @GetMapping("/{id}")
    fun getPostById(@PathVariable id: Long): ResponseEntity<PostTo> {
        val post = postService.getPostById(id)
        return ResponseEntity.ok(post)
    }

    @GetMapping
    fun getAllPosts(): ResponseEntity<List<PostTo>> {
        val posts = postService.getAllPosts()
        return ResponseEntity.ok(posts)
    }

    @PutMapping()
    fun updatePost(@RequestBody @Valid postFrom: PostFrom): ResponseEntity<PostTo> {
        val updatedPost = postService.updatePost(postFrom.id, postFrom)
        return ResponseEntity.ok(updatedPost)
    }

    @DeleteMapping("/{id}")
    fun deletePost(@PathVariable id: Long): ResponseEntity<Void> {
        postService.deletePost(id)
        return ResponseEntity.noContent().build()
    }

    @GetMapping("/by-issue/{issueId}")
    fun getPostsByIssueId(@PathVariable issueId: Long): ResponseEntity<List<PostTo>> {
        val posts = postService.getPostsByIssueId(issueId)
        return ResponseEntity.ok(posts)
    }
}