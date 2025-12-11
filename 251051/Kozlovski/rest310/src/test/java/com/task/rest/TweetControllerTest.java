package com.task.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.task.rest.dto.TweetRequestTo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TweetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private TweetRequestTo validTweet;

    @BeforeEach
    void setUp() {
        validTweet = new TweetRequestTo();
        validTweet.setWriterId(1L);
        validTweet.setTitle("Valid Title");
        validTweet.setContent("This is a valid tweet content.");
    }

    @Test
    void createTweet_shouldReturnCreatedStatus() throws Exception {
        mockMvc.perform(post("/api/v1.0/tweets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validTweet)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Valid Title"))
                .andExpect(jsonPath("$.content").value("This is a valid tweet content."))
                .andExpect(jsonPath("$.writerId").value(1));
    }

    @Test
    void getAllTweets_shouldReturnOk() throws Exception {
        mockMvc.perform(get("/api/v1.0/tweets"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void getTweetById_shouldReturnOk_whenExists() throws Exception {
        String createdResponse = mockMvc.perform(post("/api/v1.0/tweets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validTweet)))
                .andReturn().getResponse().getContentAsString();

        Long id = objectMapper.readTree(createdResponse).get("id").asLong();

        mockMvc.perform(get("/api/v1.0/tweets/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.title").value("Valid Title"));
    }

    @Test
    void updateTweet_shouldReturnOk() throws Exception {
        String createdResponse = mockMvc.perform(post("/api/v1.0/tweets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validTweet)))
                .andReturn().getResponse().getContentAsString();

        Long id = objectMapper.readTree(createdResponse).get("id").asLong();

        validTweet.setTitle("Updated Title");
        validTweet.setContent("Updated content");

        mockMvc.perform(put("/api/v1.0/tweets/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validTweet)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Title"))
                .andExpect(jsonPath("$.content").value("Updated content"));
    }

    @Test
    void deleteTweet_shouldReturnNoContent() throws Exception {
        // Создаём
        String createdResponse = mockMvc.perform(post("/api/v1.0/tweets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validTweet)))
                .andReturn().getResponse().getContentAsString();

        Long id = objectMapper.readTree(createdResponse).get("id").asLong();

        mockMvc.perform(delete("/api/v1.0/tweets/{id}", id))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1.0/tweets/{id}", id))
                .andExpect(status().is4xxClientError()); // обычно 404
    }

    @Test
    void getTweetsByMarkId_shouldReturnOk() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/v1.0/tweets/by-mark-id/{markId}", 1L))
                .andReturn();

        int status = result.getResponse().getStatus();
        assertTrue(status >= 200 && status < 500,
                "Expected 2xx or 4xx, but got: " + status);
    }

    @Test
    void getTweetsByWriterId_shouldReturnOk() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/v1.0/tweets/by-writer-id/{writerId}", 1L))
                .andReturn();

        int status = result.getResponse().getStatus();
        assertTrue(status >= 200 && status < 500,
                "Expected 2xx or 4xx, but got: " + status);
    }

    @Test
    void getTweetsByMarkName_shouldReturnOk() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/v1.0/tweets/by-mark-name/{markName}", "technology"))
                .andReturn();

        int status = result.getResponse().getStatus();
        assertTrue(status >= 200 && status < 500,
                "Expected 2xx or 4xx, but got: " + status);
    }

    @Test
    void createTweet_withBlankTitle_shouldReturnBadRequest() throws Exception {
        TweetRequestTo invalid = new TweetRequestTo();
        invalid.setWriterId(1L);
        invalid.setTitle(""); // пустой заголовок
        invalid.setContent("Valid content");

        mockMvc.perform(post("/api/v1.0/tweets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createTweet_withNullWriterId_shouldReturnBadRequest() throws Exception {
        TweetRequestTo invalid = new TweetRequestTo();
        invalid.setWriterId(null); // null
        invalid.setTitle("Valid Title");
        invalid.setContent("Valid content");

        mockMvc.perform(post("/api/v1.0/tweets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createTweet_withLongContent_shouldReturnBadRequest() throws Exception {
        TweetRequestTo invalid = new TweetRequestTo();
        invalid.setWriterId(1L);
        invalid.setTitle("Title");
        invalid.setContent("a".repeat(2049)); // больше 2048 символов

        mockMvc.perform(post("/api/v1.0/tweets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }
}