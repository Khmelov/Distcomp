package com.distcomp.publisher;

import com.distcomp.publisher.security.dto.LoginRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:publisher-sec-test;MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
        "spring.datasource.driverClassName=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
@AutoConfigureMockMvc
class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void v1EndpointsArePublic() throws Exception {
        mockMvc.perform(get("/api/v1.0/writers"))
                .andExpect(status().isOk());
    }

    @Test
    void v2RequiresJwt() throws Exception {
        mockMvc.perform(get("/api/v2.0/writers"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.errorMessage").exists())
                .andExpect(jsonPath("$.errorCode").value("40101"));
    }

    @Test
    void jwtAllowsAccessToProtectedResources() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setLogin("aleksandraguzik32@gmail.com");
        loginRequest.setPassword("qwerty123");

        MediaType json = MediaType.APPLICATION_JSON;
        String loginBody = objectMapper.writeValueAsString(loginRequest);

        MvcResult loginResult = mockMvc.perform(post("/api/v2.0/login")
                        .contentType(json)
                        .content(loginBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.access_token").exists())
                .andReturn();

        String token = objectMapper.readTree(loginResult.getResponse().getContentAsString())
                .get("access_token")
                .asText();

        String authHeader = "Bearer " + token;

        mockMvc.perform(get("/api/v2.0/writers")
                        .header("Authorization", authHeader))
                .andExpect(status().isOk());
    }
}
