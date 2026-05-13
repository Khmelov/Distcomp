package by.bsuir.task310.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AuthorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void createAuthor() throws Exception {
        String json = """
                {
                  "login": "test@gmail.com",
                  "password": "12345678",
                  "firstname": "Dmitrij",
                  "lastname": "Komasilo"
                }
                """;

        mockMvc.perform(post("/api/v1.0/authors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.login").value("test@gmail.com"))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    void getNotExistingAuthor() throws Exception {
        mockMvc.perform(get("/api/v1.0/authors/999999"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage").value("Author not found"))
                .andExpect(jsonPath("$.errorCode").value("40401"));
    }

    @Test
    void deleteAuthor() throws Exception {
        String json = """
                {
                  "login": "delete@gmail.com",
                  "password": "12345678",
                  "firstname": "Delete",
                  "lastname": "User"
                }
                """;

        String response = mockMvc.perform(post("/api/v1.0/authors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andReturn()
                .getResponse()
                .getContentAsString();

        String id = response.replaceAll(".*\"id\":(\\d+).*", "$1");

        mockMvc.perform(delete("/api/v1.0/authors/" + id))
                .andExpect(status().isNoContent());
    }
}