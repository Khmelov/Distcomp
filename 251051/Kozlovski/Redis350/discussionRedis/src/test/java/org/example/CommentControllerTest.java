package org.example;

import org.example.CassandraIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
class CommentControllerTest extends CassandraIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    void createComment() throws Exception {
        mockMvc.perform(post("/api/v1.0/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "id": 1,
                          "country": "BY",
                          "tweetId": 10,
                          "content": "hello"
                        }
                        """))
                .andExpect(status().isCreated());
    }
}