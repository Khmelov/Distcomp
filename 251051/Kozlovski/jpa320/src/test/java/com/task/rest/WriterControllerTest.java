package com.task.rest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.task.rest.dto.WriterRequestTo;
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
public class WriterControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private WriterRequestTo validWriter;

    private String uniqueLogin() {
        return "john_doe_" + System.currentTimeMillis();
    }

    @BeforeEach
    void setUp() {
        validWriter = new WriterRequestTo();
        validWriter.setLogin(uniqueLogin());       // <= max 64 символа
        validWriter.setPassword("securePass123");  // <= max ограничение по модели
        validWriter.setFirstname("John");          // <= max 64 символа
        validWriter.setLastname("Doerrr");            // <= max 64 символа
    }

    private Long extractId(String json) throws Exception {
        JsonNode node = objectMapper.readTree(json);
        assertNotNull(node, "Response JSON is null");
        assertTrue(node.has("id"), "Response JSON does NOT contain field 'id'. Response: " + json);
        return node.get("id").asLong();
    }

    @Test
    void createWriter_shouldReturnCreatedStatus() throws Exception {
        mockMvc.perform(post("/api/v1.0/writers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validWriter)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.login").value(validWriter.getLogin()))
                .andExpect(jsonPath("$.firstname").value(validWriter.getFirstname()))
                .andExpect(jsonPath("$.lastname").value(validWriter.getLastname()));
    }

    @Test
    void getAllWriters_shouldReturnOk() throws Exception {
        mockMvc.perform(get("/api/v1.0/writers"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void getWriterById_shouldReturnOk_whenExists() throws Exception {
        String created = mockMvc.perform(post("/api/v1.0/writers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validWriter)))
                .andReturn().getResponse().getContentAsString();

        Long id = extractId(created);

        mockMvc.perform(get("/api/v1.0/writers/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.login").value(validWriter.getLogin()));
    }

    @Test
    void updateWriter_shouldReturnOk() throws Exception {
        String created = mockMvc.perform(post("/api/v1.0/writers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validWriter)))
                .andReturn().getResponse().getContentAsString();

        Long id = extractId(created);

        validWriter.setFirstname("Jonathan");
        validWriter.setLastname("Smith");

        mockMvc.perform(put("/api/v1.0/writers/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validWriter)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstname").value(validWriter.getFirstname()))
                .andExpect(jsonPath("$.lastname").value(validWriter.getLastname()));
    }

    @Test
    void deleteWriter_shouldReturnNoContent() throws Exception {
        String created = mockMvc.perform(post("/api/v1.0/writers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validWriter)))
                .andReturn().getResponse().getContentAsString();

        Long id = extractId(created);

        mockMvc.perform(delete("/api/v1.0/writers/{id}", id))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1.0/writers/{id}", id))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void getWriterByTweetId_shouldReturn4xxOr2xx() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/v1.0/writers/by-tweet/{tweetId}", 1L))
                .andReturn();

        int status = result.getResponse().getStatus();
        assertTrue(status >= 200 && status < 500,
                "Expected 2xx or 4xx instead of server error (5xx). Actual: " + status);
    }

    @Test
    void createWriter_withInvalidLogin_shouldReturnBadRequest() throws Exception {
        WriterRequestTo invalid = new WriterRequestTo();
        invalid.setLogin("jo"); // меньше 4 символов
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
        invalid.setLogin(uniqueLogin());
        invalid.setPassword(""); // пустой пароль
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
        invalid.setLogin(uniqueLogin());
        invalid.setPassword("validPassword123");
        invalid.setFirstname(""); // пустое имя
        invalid.setLastname("Doe");

        mockMvc.perform(post("/api/v1.0/writers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }
}