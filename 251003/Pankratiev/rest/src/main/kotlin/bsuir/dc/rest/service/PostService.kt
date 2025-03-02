package bsuir.dc.rest.service

import bsuir.dc.rest.dto.from.PostFrom
import bsuir.dc.rest.dto.to.PostTo
import bsuir.dc.rest.mapper.toEntity
import bsuir.dc.rest.mapper.toResponse
import bsuir.dc.rest.repository.memory.PostInMemoryRepository
import org.springframework.stereotype.Service

@Service
class PostService(
    private val postRepository: PostInMemoryRepository,
) {
    fun createPost(postFrom: PostFrom): PostTo {
        val post = postFrom.toEntity()
        val savedPost = postRepository.save(post)
        return savedPost.toResponse()
    }

    fun getPostById(id: Long): PostTo {
        val post = postRepository.findById(id)
        return post.toResponse()
    }

    fun getAllPosts(): List<PostTo> =
        postRepository.findAll().map { it.toResponse() }

    fun updatePost(id: Long, postFrom: PostFrom): PostTo {
        val updatedPost = postFrom.toEntity().apply { this.id = id }
        return postRepository.update(updatedPost).toResponse()
    }

    fun deletePost(id: Long) {
        postRepository.deleteById(id)
    }

    fun getPostsByIssueId(issueId: Long): List<PostTo> {
        return postRepository.findAll()
            .filter { it.issueId == issueId }
            .map { it.toResponse() }
    }
}