package com.task.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.task.rest.dto.CommentRequestTo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private CommentRequestTo validComment;

    @BeforeEach
    void setUp() {
        validComment = new CommentRequestTo();
        validComment.setTweetId(1L);
        validComment.setContent("This is a valid comment.");
    }

    @Test
    void createComment_shouldReturnCreatedStatus() throws Exception {
        mockMvc.perform(post("/api/v1.0/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validComment)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.content").value("This is a valid comment."))
                .andExpect(jsonPath("$.tweetId").value(1));
    }

    @Test
    void getAllComments_shouldReturnOk() throws Exception {
        mockMvc.perform(get("/api/v1.0/comments"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void getCommentById_shouldReturnOk_whenExists() throws Exception {
        // Сначала создаем комментарий
        String createdResponse = mockMvc.perform(post("/api/v1.0/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validComment)))
                .andReturn().getResponse().getContentAsString();

        Long id = objectMapper.readTree(createdResponse).get("id").asLong();

        mockMvc.perform(get("/api/v1.0/comments/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id));
    }

    @Test
    void updateComment_shouldReturnOk() throws Exception {
        String createdResponse = mockMvc.perform(post("/api/v1.0/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validComment)))
                .andReturn().getResponse().getContentAsString();

        Long id = objectMapper.readTree(createdResponse).get("id").asLong();

        validComment.setContent("Updated comment");
        mockMvc.perform(put("/api/v1.0/comments/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validComment)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("Updated comment"));
    }

    @Test
    void deleteComment_shouldReturnNoContent() throws Exception {
        String createdResponse = mockMvc.perform(post("/api/v1.0/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validComment)))
                .andReturn().getResponse().getContentAsString();

        Long id = objectMapper.readTree(createdResponse).get("id").asLong();

        mockMvc.perform(delete("/api/v1.0/comments/{id}", id))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1.0/comments/{id}", id))
                .andExpect(status().is4xxClientError()); // если сервис возвращает 404
    }

    @Test
    void getCommentsByTweetId_shouldReturnOk() throws Exception {
        mockMvc.perform(get("/api/v1.0/comments/by-tweet/{tweetId}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void createComment_withInvalidContent_shouldReturnBadRequest() throws Exception {
        CommentRequestTo invalid = new CommentRequestTo();
        invalid.setTweetId(1L);
        invalid.setContent(""); // пустой контент

        mockMvc.perform(post("/api/v1.0/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createComment_withNullTweetId_shouldReturnBadRequest() throws Exception {
        CommentRequestTo invalid = new CommentRequestTo();
        invalid.setTweetId(null);
        invalid.setContent("Valid content");

        mockMvc.perform(post("/api/v1.0/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }
}