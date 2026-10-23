package com.distcomp.controller;

import com.distcomp.dto.request.CreatorRequestTo;
import com.distcomp.service.CreatorService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Controller tests of the {@code /api/v1.0/creators} resource.
 *
 * <p>The tests drive the real HTTP layer together with the real service and
 * in-memory storage, therefore they also protect the mapping and validation
 * rules of the creator aggregate.</p>
 */
@SpringBootTest
@AutoConfigureMockMvc
class CreatorControllerTest {

    private static final String URL = ApiPaths.V1 + "/creators";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CreatorService creatorService;

    @Test
    void createReturnsCreatedCreatorWithLocationAndIdentifier() throws Exception {
        String login = "creator_" + System.nanoTime();

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body(login, "password123", "Ivan", "Petrov")))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.login").value(login))
                .andExpect(jsonPath("$.firstname").value("Ivan"));
    }

    @Test
    void findByIdReturnsStoredCreator() throws Exception {
        long id = create("creator_" + System.nanoTime());

        mockMvc.perform(get(URL + "/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.lastname").value("Petrov"));
    }

    @Test
    void findByIdReturnsNotFoundForUnknownIdentifier() throws Exception {
        mockMvc.perform(get(URL + "/999999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value("40401"))
                .andExpect(jsonPath("$.errorMessage").isNotEmpty());
    }

    @Test
    void findAllContainsCreatedCreator() throws Exception {
        long id = create("creator_" + System.nanoTime());

        mockMvc.perform(get(URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.id == " + id + ")]").exists());
    }

    @Test
    void updateUsesIdentifierFromPayload() throws Exception {
        long id = create("creator_" + System.nanoTime());
        String updatedLogin = "updated_" + System.nanoTime();

        mockMvc.perform(put(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"id":%d,"login":"%s","password":"password456","firstname":"Petr","lastname":"Sidorov"}
                                """.formatted(id, updatedLogin)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.login").value(updatedLogin))
                .andExpect(jsonPath("$.firstname").value("Petr"));
    }

    @Test
    void updateRejectsTooShortLogin() throws Exception {
        long id = create("creator_" + System.nanoTime());

        mockMvc.perform(put(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"id":%d,"login":"x","password":"password456","firstname":"Petr","lastname":"Sidorov"}
                                """.formatted(id)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("40001"));
    }

    @Test
    void deleteReturnsNoContentAndSecondDeleteReturnsNotFound() throws Exception {
        long id = create("creator_" + System.nanoTime());

        mockMvc.perform(delete(URL + "/" + id))
                .andExpect(status().isNoContent());

        mockMvc.perform(delete(URL + "/" + id))
                .andExpect(status().isNotFound());
    }

    @Test
    void createRejectsTooShortPassword() throws Exception {
        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body("creator_" + System.nanoTime(), "short", "Ivan", "Petrov")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("40001"));
    }

    @Test
    void createRejectsBlankFirstname() throws Exception {
        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body("creator_" + System.nanoTime(), "password123", "", "Petrov")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void creatorIsResolvedByIssueIdentifier() throws Exception {
        long creatorId = create("creator_" + System.nanoTime());
        long issueId = createIssue(creatorId);

        mockMvc.perform(get(URL + "/by-issue/" + issueId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(creatorId));
    }

    private long create(String login) {
        return creatorService.create(new CreatorRequestTo(null, login, "password123", "Ivan", "Petrov")).id();
    }

    private long createIssue(long creatorId) throws Exception {
        String response = mockMvc.perform(post(ApiPaths.V1 + "/issues")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"creatorId":%d,"title":"Issue title","content":"Issue content"}
                                """.formatted(creatorId)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString(java.nio.charset.StandardCharsets.UTF_8);
        return new com.fasterxml.jackson.databind.ObjectMapper().readTree(response).get("id").asLong();
    }

    private String body(String login, String password, String firstname, String lastname) {
        return """
                {"login":"%s","password":"%s","firstname":"%s","lastname":"%s"}
                """.formatted(login, password, firstname, lastname);
    }
}
