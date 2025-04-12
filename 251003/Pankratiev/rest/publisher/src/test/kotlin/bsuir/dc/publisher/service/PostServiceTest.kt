package bsuir.dc.publisher.service

import bsuir.dc.publisher.dto.from.PostRequestTo
import bsuir.dc.publisher.dto.to.PostResponseTo
import bsuir.dc.publisher.entity.Issue
import bsuir.dc.publisher.entity.Post
import bsuir.dc.publisher.repository.PostRepository
import bsuir.dc.publisher.repository.IssueRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import java.util.*

class PostServiceTest {

    private val postRepository = Mockito.mock(PostRepository::class.java)
    private val issueRepository = Mockito.mock(IssueRepository::class.java)
    private val postService = PostService(postRepository, issueRepository)

    @Test
    fun `should create post`() {
        val postRequest = PostRequestTo(
            id = 0,
            issueId = 1,
            content = "This is a test post"
        )
        val issueEntity = Issue(
            id = 1,
            title = "Issue Title",
            content = "Issue Content",
            labels = mutableSetOf()
        )
        val postEntity = Post(
            id = 0,
            content = "This is a test post",
            issue = issueEntity
        )
        val savedPost = postEntity.copy(id = 1)

        `when`(issueRepository.findById(1L)).thenReturn(Optional.of(issueEntity))
        `when`(postRepository.save(postEntity)).thenReturn(savedPost)

        val result = postService.createPost(postRequest)

        assertEquals(1L, result.id)
        assertEquals("This is a test post", result.content)
        assertEquals(1L, result.issueId)
        verify(issueRepository).findById(1L)
        verify(postRepository).save(postEntity)
    }

    @Test
    fun `should throw exception if issue not found when creating post`() {
        val postRequest = PostRequestTo(
            id = 0,
            issueId = 99,
            content = "This is a test post"
        )

        `when`(issueRepository.findById(99L)).thenReturn(Optional.empty())

        assertThrows<NoSuchElementException> {
            postService.createPost(postRequest)
        }
        verify(issueRepository).findById(99L)
    }

    @Test
    fun `should get post by id`() {
        val postEntity = Post(
            id = 1,
            content = "This is a test post",
            issue = Issue(id = 1, title = "Issue Title", content = "Issue Content")
        )
        `when`(postRepository.findById(1L)).thenReturn(Optional.of(postEntity))

        val result = postService.getPostById(1L)

        assertEquals(1L, result.id)
        assertEquals("This is a test post", result.content)
        assertEquals(1L, result.issueId)
        verify(postRepository).findById(1L)
    }

    @Test
    fun `should throw exception when post not found by id`() {
        `when`(postRepository.findById(1L)).thenReturn(Optional.empty())

        assertThrows<NoSuchElementException> {
            postService.getPostById(1L)
        }
        verify(postRepository).findById(1L)
    }

    @Test
    fun `should get all posts`() {
        val posts = listOf(
            Post(id = 1, content = "Post 1", issue = Issue(id = 1, title = "Issue 1", content = "Issue Content 1")),
            Post(id = 2, content = "Post 2", issue = Issue(id = 2, title = "Issue 2", content = "Issue Content 2"))
        )
        `when`(postRepository.findAll()).thenReturn(posts)

        val result = postService.getAllPosts()

        assertEquals(2, result.size)
        assertEquals("Post 1", result[0].content)
        assertEquals("Post 2", result[1].content)
        verify(postRepository).findAll()
    }

    @Test
    fun `should update post`() {
        val postRequest = PostRequestTo(
            id = 1,
            issueId = 1,
            content = "Updated post content"
        )
        val issueEntity = Issue(
            id = 1,
            title = "Issue Title",
            content = "Issue Content"
        )
        val postEntity = Post(
            id = 1,
            content = "Updated post content",
            issue = issueEntity
        )

        `when`(issueRepository.findById(1L)).thenReturn(Optional.of(issueEntity))
        `when`(postRepository.save(postEntity)).thenReturn(postEntity)

        val result = postService.updatePost(1L, postRequest)

        assertEquals(1L, result.id)
        assertEquals("Updated post content", result.content)
        assertEquals(1L, result.issueId)
        verify(issueRepository).findById(1L)
        verify(postRepository).save(postEntity)
    }

    @Test
    fun `should throw exception if issue not found when updating post`() {
        val postRequest = PostRequestTo(
            id = 1,
            issueId = 99,
            content = "Updated post content"
        )

        `when`(issueRepository.findById(99L)).thenReturn(Optional.empty())

        assertThrows<NoSuchElementException> {
            postService.updatePost(1L, postRequest)
        }
        verify(issueRepository).findById(99L)
    }

    @Test
    fun `should delete post`() {
        val postEntity = Post(
            id = 1,
            content = "This is a test post",
            issue = Issue(id = 1, title = "Issue Title", content = "Issue Content")
        )
        `when`(postRepository.findById(1L)).thenReturn(Optional.of(postEntity))

        postService.deletePost(1L)

        verify(postRepository).findById(1L)
        verify(postRepository).deleteById(1L)
    }

    @Test
    fun `should throw exception when deleting non-existent post`() {
        `when`(postRepository.findById(1L)).thenReturn(Optional.empty())

        assertThrows<NoSuchElementException> {
            postService.deletePost(1L)
        }
        verify(postRepository).findById(1L)
    }

    @Test
    fun `should get posts by issue id`() {
        val posts = mutableListOf(
            Post(id = 1, content = "Post 1", issue = Issue(id = 1, title = "Issue 1", content = "Issue Content 1")),
            Post(id = 2, content = "Post 2", issue = Issue(id = 1, title = "Issue 1", content = "Issue Content 1"))
        )
        val issueEntity = Issue(
            id = 1,
            title = "Issue Title",
            content = "Issue Content",
            posts = posts
        )

        `when`(issueRepository.findById(1L)).thenReturn(Optional.of(issueEntity))

        val result = postService.getPostsByIssueId(1L)

        assertEquals(2, result.size)
        assertEquals("Post 1", result[0].content)
        assertEquals("Post 2", result[1].content)
        verify(issueRepository).findById(1L)
    }

    @Test
    fun `should throw exception if issue not found when getting posts by issue id`() {
        `when`(issueRepository.findById(1L)).thenReturn(Optional.empty())

        assertThrows<NoSuchElementException> {
            postService.getPostsByIssueId(1L)
        }
        verify(issueRepository).findById(1L)
    }
}
