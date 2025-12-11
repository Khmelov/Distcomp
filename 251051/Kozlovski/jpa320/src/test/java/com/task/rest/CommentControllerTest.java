package com.task.rest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.task.rest.dto.CommentRequestTo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.*;
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

    private String uniqueContent() {
        return "This is a valid comment._" + System.currentTimeMillis();
    }

    private Long createWriter() throws Exception {
        String writerJson = """
        {
            "login": "test_login_%d",
            "password": "password123",
            "firstname": "John",
            "lastname": "Doerrr"
        }
        """.formatted(System.currentTimeMillis());

        String response = mockMvc.perform(post("/api/v1.0/writers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(writerJson))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode node = objectMapper.readTree(response);
        return node.get("id").asLong();
    }

    private Long createTweet(Long writerId) throws Exception {
        String requestJson = String.format("""
        {
          "writerId": %d,
          "title": "Test tweet title",
          "content": "Tweet content",
          "marks": []
        }
        """, writerId);

        String json = mockMvc.perform(post("/api/v1.0/tweets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return extractId(json);
    }

    @BeforeEach
    void setUp() throws Exception {
        Long writerId = createWriter();
        Long tweetId = createTweet(writerId); // ← создаём Твит, гарантируя, что он существует

        validComment = new CommentRequestTo();
        validComment.setTweetId(tweetId);
        validComment.setContent(uniqueContent());
    }


    private Long extractId(String json) throws Exception {
        JsonNode node = objectMapper.readTree(json);
        assertNotNull(node, "Response JSON is null");
        assertTrue(node.has("id"), "Response JSON does NOT contain field 'id'. Response: " + json);
        return node.get("id").asLong();
    }

    @Test
    void createComment_shouldReturnCreatedStatus() throws Exception {
        mockMvc.perform(post("/api/v1.0/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validComment)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.content").value(validComment.getContent()))
                .andExpect(jsonPath("$.tweetId").value(validComment.getTweetId()));
    }

    @Test
    void getAllComments_shouldReturnOk() throws Exception {
        mockMvc.perform(get("/api/v1.0/comments"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void getCommentById_shouldReturnOk_whenExists() throws Exception {
        String created = mockMvc.perform(post("/api/v1.0/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validComment)))
                .andReturn().getResponse().getContentAsString();

        Long id = extractId(created);

        mockMvc.perform(get("/api/v1.0/comments/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.content").value(validComment.getContent()));
    }

    @Test
    void updateComment_shouldReturnOk() throws Exception {
        String created = mockMvc.perform(post("/api/v1.0/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validComment)))
                .andReturn().getResponse().getContentAsString();

        Long id = extractId(created);

        validComment.setContent("Updated comment " + System.currentTimeMillis());
        mockMvc.perform(put("/api/v1.0/comments/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validComment)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value(validComment.getContent()))
                .andExpect(jsonPath("$.id").value(id));
    }

    @Test
    void deleteComment_shouldReturnNoContent() throws Exception {
        String created = mockMvc.perform(post("/api/v1.0/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validComment)))
                .andReturn().getResponse().getContentAsString();

        Long id = extractId(created);

        mockMvc.perform(delete("/api/v1.0/comments/{id}", id))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1.0/comments/{id}", id))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void getCommentsByTweetId_shouldReturn2xxOr4xx() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/v1.0/comments/by-tweet/{tweetId}", 1L))
                .andReturn();

        int status = result.getResponse().getStatus();
        assertTrue(status >= 200 && status < 500,
                "Expected 2xx or 4xx instead of server error (5xx). Actual: " + status);
    }

    @Test
    void createComment_withInvalidContent_shouldReturnBadRequest() throws Exception {
        CommentRequestTo invalid = new CommentRequestTo();
        invalid.setTweetId(1L);
        invalid.setContent("");

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