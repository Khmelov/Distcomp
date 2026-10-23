package com.distcomp.controller;

import com.distcomp.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Controller tests of the {@code /api/v1.0/tags} resource.
 */
class TagControllerTest extends AbstractIntegrationTest {

    private static final String URL = ApiPaths.V1 + "/tags";

    @Test
    void createStoresTagWithGeneratedIdentifier() throws Exception {
        String name = "tag" + uniqueSuffix();

        mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"" + name + "\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value(name));
    }

    @Test
    void createRejectsDuplicateName() throws Exception {
        String name = "tag" + uniqueSuffix();

        mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"" + name + "\"}"))
                .andExpect(status().isCreated());

        mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"" + name + "\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void createRejectsTooShortName() throws Exception {
        mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"x\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("40001"));
    }

    @Test
    void updateChangesName() throws Exception {
        long id = createTag("tag" + uniqueSuffix());
        String updated = "updated" + uniqueSuffix();

        mockMvc.perform(put(URL).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":" + id + ",\"name\":\"" + updated + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(updated));
    }

    @Test
    void deleteRemovesTagAndSecondDeleteIsRejected() throws Exception {
        long id = createTag("tag" + uniqueSuffix());

        mockMvc.perform(delete(URL + "/" + id)).andExpect(status().isNoContent());
        mockMvc.perform(delete(URL + "/" + id)).andExpect(status().isNotFound());
    }

    private long createTag(String name) throws Exception {
        String response = mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"" + name + "\"}"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return new com.fasterxml.jackson.databind.ObjectMapper().readTree(response).get("id").asLong();
    }
}
