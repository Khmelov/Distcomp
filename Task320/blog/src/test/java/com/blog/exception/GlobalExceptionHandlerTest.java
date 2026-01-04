package com.blog.exception;

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
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldHandleResourceNotFoundException() throws Exception {
        mockMvc.perform(get("/api/v1.0/editors/99999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").exists())
                .andExpect(jsonPath("$.errorMessage").exists())
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void shouldHandleValidationException() throws Exception {
        String invalidEditorJson = "{\"login\":\"a\",\"password\":\"short\",\"firstname\":\"\",\"lastname\":\"\"}";

        mockMvc.perform(post("/api/v1.0/editors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidEditorJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").exists())
                .andExpect(jsonPath("$.errors").exists())
                .andExpect(jsonPath("$.errors.login").exists())
                .andExpect(jsonPath("$.errors.password").exists());
    }

    @Test
    void shouldHandleTypeMismatchException() throws Exception {
        mockMvc.perform(get("/api/v1.0/editors/not-a-number"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").exists())
                .andExpect(jsonPath("$.errorMessage").exists());
    }
}