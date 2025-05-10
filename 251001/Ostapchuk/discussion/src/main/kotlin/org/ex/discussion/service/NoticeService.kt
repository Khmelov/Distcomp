package org.ex.discussion.service

import org.ex.discussion.dto.request.PostRequestDTO
import org.ex.discussion.dto.response.PostResponseDTO
import org.ex.discussion.exception.NotFoundException
import org.ex.discussion.mapper.PostMapper
import org.ex.discussion.repository.PostRepository
import org.springframework.stereotype.Service

@Service
class PostService(
    private val postMapper: PostMapper,
    private val postRepository: PostRepository) {

    fun getAllPosts(): List<PostResponseDTO> = postRepository.findAll().map { postMapper.toDto(it) }


    fun getPostById(postId: Long): PostResponseDTO {
        return postRepository.findById(postId)
            .map { postMapper.toDto(it) }
            .orElseThrow {throw NotFoundException("Post with id: $postId not found")}
    }

    fun createPost(postRequest: PostRequestDTO): PostResponseDTO {
        val post = postMapper.toEntity(postRequest)
        postRepository.save(post)
        return postMapper.toDto(post)
    }

    fun updatePost(postRequest: PostRequestDTO): PostResponseDTO {
        return postRepository.findById(postRequest.id)
            .map {
                it.content = postRequest.content
                it.tweetId = postRequest.tweetId
                val updated = it.copy(content = postRequest.content, tweetId = postRequest.tweetId)
                return@map postMapper.toDto(postRepository.save(updated))
            }.orElseThrow {throw NotFoundException("Post with id: ${postRequest.id}")}
    }

    fun deletePost(postId: Long): PostResponseDTO {
        return postRepository.findById(postId)
            .map {
                postRepository.delete(it.id, it.country)
                return@map it
            }.map { postMapper.toDto(it) }
            .orElseThrow{ throw NotFoundException("Post with id: $postId not found") }
    }
}