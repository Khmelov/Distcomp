package bsuir.dc.publisher.controller

import bsuir.dc.publisher.dto.from.PostRequestTo
import bsuir.dc.publisher.dto.to.PostResponseTo
import bsuir.dc.publisher.service.PostService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.reactive.function.client.WebClient

@RestController
@RequestMapping("/api/v1.0/posts")
class PostController(
    private val postService: PostService,
    private val webClient: WebClient
) {

    @PostMapping
    fun createPost(@RequestBody @Valid postRequestTo: PostRequestTo): ResponseEntity<PostResponseTo> {
        val createdPost = postService.createPost(postRequestTo)
        sendPostRequest(createdPost)
        return ResponseEntity(createdPost, HttpStatus.CREATED)
    }

    @GetMapping("/{id}")
    fun getPostById(@PathVariable id: Long): ResponseEntity<PostResponseTo> {
        val post = postService.getPostById(id)
        return ResponseEntity.ok(post)
    }

    @GetMapping
    fun getAllPosts(): ResponseEntity<List<PostResponseTo>> {
        val posts = postService.getAllPosts()
        return ResponseEntity.ok(posts)
    }

    @PutMapping()
    fun updatePost(@RequestBody @Valid postRequestTo: PostRequestTo): ResponseEntity<PostResponseTo> {
        val updatedPost = postService.updatePost(postRequestTo.id, postRequestTo)
        sendPutRequest(updatedPost)
        return ResponseEntity.ok(updatedPost)
    }

    @DeleteMapping("/{id}")
    fun deletePost(@PathVariable id: Long): ResponseEntity<Void> {
        sendDeleteRequest(id)
        postService.deletePost(id)
        return ResponseEntity.noContent().build()
    }

    @GetMapping("/by-issue/{issueId}")
    fun getPostsByIssueId(@PathVariable issueId: Long): ResponseEntity<List<PostResponseTo>> {
        val posts = postService.getPostsByIssueId(issueId)
        return ResponseEntity.ok(posts)
    }

    private val url = "http://localhost:24130/api/v1.0/posts"

    fun sendPostRequest(body: PostResponseTo) {
        webClient.post()
            .uri(url)
            .bodyValue(body)
            .retrieve()
            .toBodilessEntity()
            .subscribe()
    }

    fun sendPutRequest(body: PostResponseTo) {
        webClient.put()
            .uri(url)
            .bodyValue(body)
            .retrieve()
            .toBodilessEntity()
            .subscribe()
    }

    fun sendDeleteRequest(id: Long) {
        webClient.delete()
            .uri("$url/$id")
            .retrieve()
            .toBodilessEntity()
            .subscribe()
    }
}