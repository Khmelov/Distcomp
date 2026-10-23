package com.distcomp.controller;

import com.distcomp.dto.request.CreatorRequestTo;
import com.distcomp.dto.request.TagRequestTo;
import com.distcomp.service.CreatorService;
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
 * Controller tests of the {@code /api/v1.0/issues} resource.
 */
@SpringBootTest
@AutoConfigureMockMvc
class IssueControllerTest {

    private static final String URL = ApiPaths.V1 + "/issues";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CreatorService creatorService;

    @Autowired
    private TagService tagService;

    @Test
    void createStampsTimestampsAndReturnsIdentifier() throws Exception {
        long creatorId = createCreator();

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"creatorId":%d,"title":"Issue title","content":"Issue content"}
                                """.formatted(creatorId)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.creatorId").value(creatorId))
                .andExpect(jsonPath("$.created").isNotEmpty())
                .andExpect(jsonPath("$.modified").isNotEmpty());
    }

    @Test
    void createRejectsUnknownCreator() throws Exception {
        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"creatorId":999999,"title":"Issue title","content":"Issue content"}
                                """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value("40401"));
    }

    @Test
    void createRejectsTooShortTitle() throws Exception {
        long creatorId = createCreator();

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"creatorId":%d,"title":"x","content":"Issue content"}
                                """.formatted(creatorId)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("40001"));
    }

    @Test
    void createRejectsTooShortContent() throws Exception {
        long creatorId = createCreator();

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"creatorId":%d,"title":"Issue title","content":"abc"}
                                """.formatted(creatorId)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createRejectsUnknownTag() throws Exception {
        long creatorId = createCreator();

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"creatorId":%d,"title":"Issue title","content":"Issue content","tagIds":[999999]}
                                """.formatted(creatorId)))
                .andExpect(status().isNotFound());
    }

    @Test
    void readUpdateAndDeleteRoundTrip() throws Exception {
        long creatorId = createCreator();
        long tagId = tagService.create(new TagRequestTo(null, "tag_" + System.nanoTime())).id();

        String created = mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"creatorId":%d,"title":"First title","content":"First content","tagIds":[%d]}
                                """.formatted(creatorId, tagId)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tagIds[0]").value(tagId))
                .andReturn().getResponse().getContentAsString(java.nio.charset.StandardCharsets.UTF_8);

        long issueId = new com.fasterxml.jackson.databind.ObjectMapper()
                .readTree(created).get("id").asLong();

        mockMvc.perform(get(URL + "/" + issueId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("First title"));

        mockMvc.perform(put(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"id":%d,"creatorId":%d,"title":"Updated title","content":"Updated content"}
                                """.formatted(issueId, creatorId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated title"));

        mockMvc.perform(delete(URL + "/" + issueId))
                .andExpect(status().isNoContent());

        mockMvc.perform(get(URL + "/" + issueId))
                .andExpect(status().isNotFound());
    }

    @Test
    void issuesAreFoundByCreatorAndByTag() throws Exception {
        long creatorId = createCreator();
        long tagId = tagService.create(new TagRequestTo(null, "tag_" + System.nanoTime())).id();

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"creatorId":%d,"title":"Tagged title","content":"Tagged content","tagIds":[%d]}
                                """.formatted(creatorId, tagId)))
                .andExpect(status().isCreated());

        mockMvc.perform(get(URL + "/by-creator/" + creatorId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].creatorId").value(creatorId));

        mockMvc.perform(get(URL + "/by-tag/" + tagId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].tagIds[0]").value(tagId));
    }

    @Test
    void findAllReturnsCollection() throws Exception {
        mockMvc.perform(get(URL))
                .andExpect(status().isOk());
    }

    private long createCreator() {
        return creatorService.create(new CreatorRequestTo(
                null, "creator_" + System.nanoTime(), "password123", "Ivan", "Petrov")).id();
    }
}
