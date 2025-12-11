package com.task.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.task.rest.dto.WriterRequestTo;
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
public class WriterControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private WriterRequestTo validWriter;

    @BeforeEach
    void setUp() {
        validWriter = new WriterRequestTo();
        validWriter.setLogin("john_doe");
        validWriter.setPassword("securePassword123");
        validWriter.setFirstname("John");
        validWriter.setLastname("Doe");
    }

    @Test
    void createWriter_shouldReturnCreatedStatus() throws Exception {
        mockMvc.perform(post("/api/v1.0/writers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validWriter)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.login").value("john_doe"))
                .andExpect(jsonPath("$.firstname").value("John"))
                .andExpect(jsonPath("$.lastname").value("Doe"));
    }

    @Test
    void getAllWriters_shouldReturnOk() throws Exception {
        mockMvc.perform(get("/api/v1.0/writers"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void getWriterById_shouldReturnOk_whenExists() throws Exception {
        String createdResponse = mockMvc.perform(post("/api/v1.0/writers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validWriter)))
                .andReturn().getResponse().getContentAsString();

        Long id = objectMapper.readTree(createdResponse).get("id").asLong();

        mockMvc.perform(get("/api/v1.0/writers/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.login").value("john_doe"));
    }

    @Test
    void updateWriter_shouldReturnOk() throws Exception {

        String createdResponse = mockMvc.perform(post("/api/v1.0/writers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validWriter)))
                .andReturn().getResponse().getContentAsString();

        Long id = objectMapper.readTree(createdResponse).get("id").asLong();


        validWriter.setFirstname("Jonathan");
        validWriter.setLastname("Smith");

        mockMvc.perform(put("/api/v1.0/writers/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validWriter)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstname").value("Jonathan"))
                .andExpect(jsonPath("$.lastname").value("Smith"));
    }

    @Test
    void deleteWriter_shouldReturnNoContent() throws Exception {
        // Создаём
        String createdResponse = mockMvc.perform(post("/api/v1.0/writers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validWriter)))
                .andReturn().getResponse().getContentAsString();

        Long id = objectMapper.readTree(createdResponse).get("id").asLong();

        mockMvc.perform(delete("/api/v1.0/writers/{id}", id))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1.0/writers/{id}", id))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void getWriterByTweetId_shouldReturn4xx() throws Exception {

        mockMvc.perform(get("/api/v1.0/writers/by-tweet/{tweetId}", 1L))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void createWriter_withInvalidLogin_shouldReturnBadRequest() throws Exception {
        WriterRequestTo invalid = new WriterRequestTo();
        invalid.setLogin("jo");
        invalid.setPassword("securePassword123");
        invalid.setFirstname("John");
        invalid.setLastname("Doe");

        mockMvc.perform(post("/api/v1.0/writers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createWriter_withBlankPassword_shouldReturnBadRequest() throws Exception {
        WriterRequestTo invalid = new WriterRequestTo();
        invalid.setLogin("valid_login");
        invalid.setPassword("");
        invalid.setFirstname("John");
        invalid.setLastname("Doe");

        mockMvc.perform(post("/api/v1.0/writers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createWriter_withNullFirstname_shouldReturnBadRequest() throws Exception {
        WriterRequestTo invalid = new WriterRequestTo();
        invalid.setLogin("valid_login");
        invalid.setPassword("validPassword123");
        invalid.setFirstname("");
        invalid.setLastname("Doe");

        mockMvc.perform(post("/api/v1.0/writers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }
}