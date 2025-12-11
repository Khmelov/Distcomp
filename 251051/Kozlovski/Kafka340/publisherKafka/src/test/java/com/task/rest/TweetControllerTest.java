package com.task.rest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.task.rest.dto.TweetRequestTo;
import com.task.rest.dto.WriterRequestTo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
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

    private String uniqueTitle() {
        return "Valid Title_" + System.currentTimeMillis();
    }

    /** Создание Writer через DTO и ObjectMapper */
    private Long createWriter() throws Exception {
        WriterRequestTo writerRequest = new WriterRequestTo();
        writerRequest.setLogin("test_login_" + System.currentTimeMillis());
        writerRequest.setPassword("password123");
        writerRequest.setFirstname("John");
        writerRequest.setLastname("Doerr");

        String response = mockMvc.perform(post("/api/v1.0/writers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(writerRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode node = objectMapper.readTree(response);
        return node.get("id").asLong();
    }

    @BeforeEach
    void setUp() throws Exception {
        Long writerId = createWriter(); // создаём нового writer
        validTweet = new TweetRequestTo();
        validTweet.setWriterId(writerId);  // используем его id
        validTweet.setTitle(uniqueTitle());
        validTweet.setContent("This is a valid tweet content.");
        validTweet.setMarks(List.of()); // пустой список
    }

    private Long extractId(String json) throws Exception {
        JsonNode node = objectMapper.readTree(json);
        assertNotNull(node, "Response JSON is null");
        assertTrue(node.has("id"), "Response JSON does NOT contain field 'id'. Response: " + json);
        return node.get("id").asLong();
    }

    @Test
    void createTweet_shouldReturnCreatedStatus() throws Exception {
        mockMvc.perform(post("/api/v1.0/tweets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validTweet)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value(validTweet.getTitle()))
                .andExpect(jsonPath("$.content").value(validTweet.getContent()))
                .andExpect(jsonPath("$.writerId").value(validTweet.getWriterId()));
    }

    @Test
    void getAllTweets_shouldReturnOk() throws Exception {
        mockMvc.perform(get("/api/v1.0/tweets"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void getTweetById_shouldReturnOk_whenExists() throws Exception {
        String created = mockMvc.perform(post("/api/v1.0/tweets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validTweet)))
                .andReturn().getResponse().getContentAsString();

        Long id = extractId(created);

        mockMvc.perform(get("/api/v1.0/tweets/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.title").value(validTweet.getTitle()));
    }

    @Test
    void updateTweet_shouldReturnOk() throws Exception {
        String created = mockMvc.perform(post("/api/v1.0/tweets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validTweet)))
                .andReturn().getResponse().getContentAsString();

        Long id = extractId(created);

        validTweet.setTitle("Updated Title " + System.currentTimeMillis());
        validTweet.setContent("Updated content");

        mockMvc.perform(put("/api/v1.0/tweets/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validTweet)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(validTweet.getTitle()))
                .andExpect(jsonPath("$.content").value(validTweet.getContent()));
    }

    @Test
    void deleteTweet_shouldReturnNoContent() throws Exception {
        String created = mockMvc.perform(post("/api/v1.0/tweets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validTweet)))
                .andReturn().getResponse().getContentAsString();

        Long id = extractId(created);

        mockMvc.perform(delete("/api/v1.0/tweets/{id}", id))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1.0/tweets/{id}", id))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void getTweetsByMarkId_shouldReturn2xxOr4xx() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/v1.0/tweets/by-mark-id/{markId}", 1L))
                .andReturn();

        int status = result.getResponse().getStatus();
        assertTrue(status >= 200 && status < 500,
                "Expected 2xx or 4xx, but got: " + status);
    }

    @Test
    void getTweetsByWriterId_shouldReturn2xxOr4xx() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/v1.0/tweets/by-writer-id/{writerId}", 1L))
                .andReturn();

        int status = result.getResponse().getStatus();
        assertTrue(status >= 200 && status < 500,
                "Expected 2xx or 4xx, but got: " + status);
    }

    @Test
    void getTweetsByMarkName_shouldReturn2xxOr4xx() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/v1.0/tweets/by-mark-name/{markName}", "technology"))
                .andReturn();

        int status = result.getResponse().getStatus();
        assertTrue(status >= 200 && status < 500,
                "Expected 2xx or 4xx, but got: " + status);
    }

    @Test
    void createTweet_withBlankTitle_shouldReturnBadRequest() throws Exception {
        TweetRequestTo invalid = new TweetRequestTo();
        invalid.setWriterId(validTweet.getWriterId());
        invalid.setTitle("");
        invalid.setContent("Valid content");

        mockMvc.perform(post("/api/v1.0/tweets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createTweet_withNullWriterId_shouldReturnBadRequest() throws Exception {
        TweetRequestTo invalid = new TweetRequestTo();
        invalid.setWriterId(null);
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
        invalid.setWriterId(validTweet.getWriterId());
        invalid.setTitle("Title");
        invalid.setContent("a".repeat(2049));

        mockMvc.perform(post("/api/v1.0/tweets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }
}
