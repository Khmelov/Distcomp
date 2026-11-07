package com.distcomp.controller;

import com.distcomp.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Controller tests of the {@code /api/v1.0/creators} resource.
 */
class CreatorControllerTest extends AbstractIntegrationTest {

    private static final String URL = ApiPaths.V1 + "/creators";

    @Test
    void createPersistsCreatorInDatabase() throws Exception {
        String login = "creator_" + uniqueSuffix();

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(creator(login)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.login").value(login));
    }

    @Test
    void createRejectsDuplicateLogin() throws Exception {
        String login = "creator_" + uniqueSuffix();

        mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON).content(creator(login)))
                .andExpect(status().isCreated());

        mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON).content(creator(login)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.errorCode").value("40301"));
    }

    @Test
    void createRejectsInvalidLoginLength() throws Exception {
        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(creator("x")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("40001"));
    }

    @Test
    void findByIdReturnsCreatorAndNotFoundAfterDeletion() throws Exception {
        long id = createCreator("creator_" + uniqueSuffix());

        mockMvc.perform(get(URL + "/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id));

        mockMvc.perform(delete(URL + "/" + id)).andExpect(status().isNoContent());

        mockMvc.perform(get(URL + "/" + id)).andExpect(status().isNotFound());
    }

    @Test
    void findAllSupportsPagingAndSorting() throws Exception {
        String login = "creator_" + uniqueSuffix();
        createCreator(login);

        mockMvc.perform(get(URL).param("page", "0").param("size", "1").param("sort", "login,desc"))
                .andExpect(status().isOk())
                .andExpect(header().exists("X-Total-Count"))
                .andExpect(jsonPath("$.length()").value(1));

        mockMvc.perform(get(URL).param("login", login))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].login").value(login));
    }

    @Test
    void findAllRejectsUnsupportedSortProperty() throws Exception {
        mockMvc.perform(get(URL).param("sort", "password,asc"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateUsesIdentifierFromPayload() throws Exception {
        long id = createCreator("creator_" + uniqueSuffix());
        String updated = "updated_" + uniqueSuffix();

        mockMvc.perform(put(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"id":%d,"login":"%s","password":"password456","firstname":"Petr","lastname":"Sidorov"}
                                """.formatted(id, updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.login").value(updated))
                .andExpect(jsonPath("$.firstname").value("Petr"));
    }

    @Test
    void deleteRemovesCreatorAndDependentIssues() throws Exception {
        long creatorId = createCreator("creator_" + uniqueSuffix());
        String issueResponse = mockMvc.perform(post(ApiPaths.V1 + "/issues")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"creatorId":%d,"title":"Title %s","content":"Some content"}
                                """.formatted(creatorId, uniqueSuffix())))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        long issueId = new com.fasterxml.jackson.databind.ObjectMapper()
                .readTree(issueResponse).get("id").asLong();

        mockMvc.perform(delete(URL + "/" + creatorId)).andExpect(status().isNoContent());
        mockMvc.perform(get(ApiPaths.V1 + "/issues/" + issueId)).andExpect(status().isNotFound());
    }

    private long createCreator(String login) throws Exception {
        String response = mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(creator(login)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return new com.fasterxml.jackson.databind.ObjectMapper().readTree(response).get("id").asLong();
    }

    private String creator(String login) {
        return """
                {"login":"%s","password":"password123","firstname":"Ivan","lastname":"Petrov"}
                """.formatted(login);
    }
}
