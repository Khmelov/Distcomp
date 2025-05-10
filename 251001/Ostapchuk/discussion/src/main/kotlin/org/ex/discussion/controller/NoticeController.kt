package org.ex.discussion.controller

import org.ex.discussion.dto.request.PostRequestDTO
import org.ex.discussion.dto.response.PostResponseDTO
import org.ex.discussion.service.PostService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/posts")
class PostController(val postService: PostService) {

    @GetMapping
    fun getAllPosts(): List<PostResponseDTO> = postService.getAllPosts()

    @GetMapping("/{id}")
    fun getPostById(@PathVariable id: Long): PostResponseDTO = postService.getPostById(id)

    @PostMapping
    fun createPost(@RequestBody postRequest: PostRequestDTO): ResponseEntity<PostResponseDTO> {
        return ResponseEntity.status(HttpStatus.CREATED).body(postService.createPost(postRequest))
    }

    @PutMapping
    fun updatePost(@RequestBody postRequest: PostRequestDTO): ResponseEntity<PostResponseDTO> {
        println("Received updating post request $postRequest")
        return ResponseEntity.ok(postService.updatePost(postRequest))
    }

    @DeleteMapping("/{id}")
    fun deletePost(@PathVariable("id") postId: Long): ResponseEntity<Void> {
        postService.deletePost(postId)
        return ResponseEntity.ok().build()
    }
}