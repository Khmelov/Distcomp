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
        return "writer_" + System.currentTimeMillis();
    }

    @BeforeEach
    void setUp() {
        validWriter = new WriterRequestTo();
        validWriter.setLogin(uniqueLogin());
        validWriter.setPassword("pass12345");
        validWriter.setFirstname("John");
        validWriter.setLastname("Smith");
    }

    private Long extractId(String json) throws Exception {
        JsonNode node = objectMapper.readTree(json);
        assertNotNull(node, "JSON response is null");
        assertTrue(node.has("id"), "Response does not contain 'id': " + json);
        return node.get("id").asLong();
    }

    @Test
    void createWriter_shouldReturnCreated() throws Exception {
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

        validWriter.setFirstname("UpdatedName");
        validWriter.setLastname("UpdatedSurname");

        mockMvc.perform(put("/api/v1.0/writers/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validWriter)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstname").value("UpdatedName"))
                .andExpect(jsonPath("$.lastname").value("UpdatedSurname"));
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

        // Проверяем, что writer удалён
        mockMvc.perform(get("/api/v1.0/writers/{id}", id))
                .andExpect(status().is4xxClientError());
    }


    @Test
    void createWriter_withInvalidLogin_shouldReturnBadRequest() throws Exception {
        WriterRequestTo invalid = new WriterRequestTo();
        invalid.setLogin("ab");       // слишком коротко
        invalid.setPassword("123456");
        invalid.setFirstname("John");
        invalid.setLastname("Doe");

        mockMvc.perform(post("/api/v1.0/writers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createWriter_withEmptyPassword_shouldReturnBadRequest() throws Exception {
        WriterRequestTo invalid = new WriterRequestTo();
        invalid.setLogin(uniqueLogin());
        invalid.setPassword(""); // пусто
        invalid.setFirstname("John");
        invalid.setLastname("Doe");

        mockMvc.perform(post("/api/v1.0/writers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createWriter_withBlankFirstname_shouldReturnBadRequest() throws Exception {
        WriterRequestTo invalid = new WriterRequestTo();
        invalid.setLogin(uniqueLogin());
        invalid.setPassword("validPass");
        invalid.setFirstname(""); // пустое
        invalid.setLastname("Doe");

        mockMvc.perform(post("/api/v1.0/writers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }
}