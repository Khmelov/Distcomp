package com.distcomp.controller;

import com.distcomp.dto.request.TagRequestTo;
import com.distcomp.service.TagService;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Controller tests of the {@code /api/v1.0/tags} resource.
 */
@SpringBootTest
@AutoConfigureMockMvc
class TagControllerTest {

    private static final String URL = ApiPaths.V1 + "/tags";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TagService tagService;

    @Test
    void createAndReadTag() throws Exception {
        String name = "tag_" + System.nanoTime();

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"" + name + "\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value(name));
    }

    @Test
    void createRejectsTooShortName() throws Exception {
        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"x\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("40001"));
    }

    @Test
    void updateChangesName() throws Exception {
        long id = tagService.create(new TagRequestTo(null, "tag_" + System.nanoTime())).id();
        String updated = "updated_" + System.nanoTime();

        mockMvc.perform(put(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":" + id + ",\"name\":\"" + updated + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(updated));
    }

    @Test
    void deleteRemovesTag() throws Exception {
        long id = tagService.create(new TagRequestTo(null, "tag_" + System.nanoTime())).id();

        mockMvc.perform(delete(URL + "/" + id))
                .andExpect(status().isNoContent());

        mockMvc.perform(get(URL + "/" + id))
                .andExpect(status().isNotFound());
    }

    @Test
    void findAllReturnsCollection() throws Exception {
        tagService.create(new TagRequestTo(null, "tag_" + System.nanoTime()));

        mockMvc.perform(get(URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}
