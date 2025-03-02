package bsuir.dc.rest

import bsuir.dc.rest.dto.from.PostFrom
import bsuir.dc.rest.service.PostService
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class PostServiceTest {

    @Autowired
    private lateinit var postService: PostService

    @Test
    fun createPostTest() {
        val postFrom = PostFrom(content = "Content", issueId = 1)
        val createdPost = postService.createPost(postFrom)
        assertNotNull(createdPost.id)
        assertEquals("Content", createdPost.content)
        assertEquals(1, createdPost.issueId)
    }

    @Test
    fun getPostByIdTest() {
        val postFrom = PostFrom(content = "Content", issueId = 2)
        val createdPost = postService.createPost(postFrom)
        val foundPost = postService.getPostById(createdPost.id)
        assertEquals(createdPost.id, foundPost.id)
    }

    @Test
    fun getAllPostsTest() {
        postService.createPost(PostFrom(content = "Content 1", issueId = 1))
        postService.createPost(PostFrom(content = "Content 2", issueId = 1))
        val posts = postService.getAllPosts()
        assertTrue(posts.any { it.content == "Content 1" })
        assertTrue(posts.any { it.content == "Content 2" })
    }

    @Test
    fun updatePostTest() {
        val postFrom = PostFrom(content = "Old Content", issueId = 1)
        val createdPost = postService.createPost(postFrom)
        val updatedPostFrom = PostFrom(content = "New Content", issueId = 1)
        val updatedPost = postService.updatePost(createdPost.id, updatedPostFrom)
        assertEquals(createdPost.id, updatedPost.id)
        assertEquals("New Content", updatedPost.content)
    }

    @Test
    fun deletePostTest() {
        val postFrom = PostFrom(content = "Content", issueId = 1)
        val createdPost = postService.createPost(postFrom)
        postService.deletePost(createdPost.id)
        assertThrows<NoSuchElementException> {
            postService.getPostById(createdPost.id)
        }
    }

    @Test
    fun getPostsByIssueIdTest() {
        val post1 = postService.createPost(PostFrom(content = "Content 1", issueId = 10))
        val post2 = postService.createPost(PostFrom(content = "Content 2", issueId = 10))
        postService.createPost(PostFrom(content = "Content 3", issueId = 20))
        val posts = postService.getPostsByIssueId(10)
        assertTrue(posts.any { it.id == post1.id })
        assertTrue(posts.any { it.id == post2.id })
        assertFalse(posts.any { it.issueId == 20L })
    }
}
