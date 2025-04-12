package bsuir.dc.publisher.controller

import bsuir.dc.publisher.dto.from.PostRequestTo
import bsuir.dc.publisher.dto.to.PostResponseTo
import bsuir.dc.publisher.service.PostService
import bsuir.dc.publisher.controller.PostController
import com.fasterxml.jackson.databind.ObjectMapper
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.Mockito.doNothing
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(controllers = [PostController::class])
@Import(PostControllerTest.TestConfig::class, PostController::class)
class PostControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var postService: PostService

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Configuration
    class TestConfig {
        @Bean
        fun postService(): PostService = Mockito.mock(PostService::class.java)
    }

    @Test
    fun `should create a post`() {
        val postRequest = PostRequestTo(id = 0, issueId = 1, content = "Test content")
        val postResponse = PostResponseTo(id = 1, issueId = 1, content = "Test content")

        `when`(postService.createPost(postRequest)).thenReturn(postResponse)

        mockMvc.perform(
            post("/api/v1.0/posts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(postRequest))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.issueId").value(1))
            .andExpect(jsonPath("$.content").value("Test content"))
    }

    @Test
    fun `should get post by id`() {
        val postResponse = PostResponseTo(id = 1, issueId = 1, content = "Test content")
        `when`(postService.getPostById(1L)).thenReturn(postResponse)

        mockMvc.perform(get("/api/v1.0/posts/1"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.issueId").value(1))
            .andExpect(jsonPath("$.content").value("Test content"))
    }

    @Test
    fun `should get all posts`() {
        val posts = listOf(
            PostResponseTo(id = 1, issueId = 1, content = "Test content"),
            PostResponseTo(id = 2, issueId = 2, content = "Another content")
        )
        `when`(postService.getAllPosts()).thenReturn(posts)

        mockMvc.perform(get("/api/v1.0/posts"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$", hasSize<Any>(2)))
    }

    @Test
    fun `should update post`() {
        val postRequest = PostRequestTo(id = 1, issueId = 1, content = "Updated content")
        val postResponse = PostResponseTo(id = 1, issueId = 1, content = "Updated content")
        `when`(postService.updatePost(1L, postRequest)).thenReturn(postResponse)

        mockMvc.perform(
            put("/api/v1.0/posts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(postRequest))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.content").value("Updated content"))
    }

    @Test
    fun `should delete post`() {
        doNothing().`when`(postService).deletePost(1L)

        mockMvc.perform(delete("/api/v1.0/posts/1"))
            .andExpect(status().isNoContent)
    }

    @Test
    fun `should get posts by issue id`() {
        val posts = listOf(
            PostResponseTo(id = 1, issueId = 1, content = "Test content"),
            PostResponseTo(id = 2, issueId = 1, content = "Another content")
        )
        `when`(postService.getPostsByIssueId(1L)).thenReturn(posts)

        mockMvc.perform(get("/api/v1.0/posts/by-issue/1"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$", hasSize<Any>(2)))
    }
}
