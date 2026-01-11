package by.rest.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles; 
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")  // Теперь импорт есть
class EditorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void createEditor() throws Exception {
        String json = """
            {
                "login": "maryaagu@mail.ru",
                "password": "password123",
                "firstname": "Марина",
                "lastname": "Дорофей"
            }
            """;

        mockMvc.perform(post("/api/v1.0/editors")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.login").value("maryaagu@mail.ru"))
                .andExpect(jsonPath("$.firstname").value("Марина"))
                .andExpect(jsonPath("$.lastname").value("Дорофей"));
    }
}