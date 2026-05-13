package by.bsuir.distcomp;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import by.bsuir.distcomp.dto.CommentDto;
import by.bsuir.distcomp.dto.IssueDto;
import by.bsuir.distcomp.dto.TagDto;
import by.bsuir.distcomp.dto.WriterDto;
import by.bsuir.distcomp.repository.CommentRepository;
import by.bsuir.distcomp.repository.IssueRepository;
import by.bsuir.distcomp.repository.TagRepository;
import by.bsuir.distcomp.repository.WriterRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class RestCrudIntegrationTest {
    private static final String API = "/api/v1.0";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private IssueRepository issueRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private WriterRepository writerRepository;

    @BeforeEach
    void cleanDatabase() {
        commentRepository.deleteAll();
        issueRepository.deleteAll();
        tagRepository.deleteAll();
        writerRepository.deleteAll();
    }

    @Test
    void writerCrudAndValidation() throws Exception {
        long id = createWriter("writer1@mail.ru");

        mockMvc.perform(get(API + "/writers/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.login").value("writer1@mail.ru"));

        mockMvc.perform(get(API + "/writers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        mockMvc.perform(put(API + "/writers/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(new WriterDto(id, "writer2@mail.ru", "password123",
                                "Dmitry", "Loginov"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.login").value("writer2@mail.ru"));

        mockMvc.perform(post(API + "/writers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(new WriterDto(null, "x", "short", "D", "L"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage").exists())
                .andExpect(jsonPath("$.errorCode").value("40001"));

        mockMvc.perform(delete(API + "/writers/{id}", id))
                .andExpect(status().isNoContent());
    }

    @Test
    void issueCommentAndTagCrud() throws Exception {
        long writerId = createWriter("writer3@mail.ru");
        long issueId = createIssue(writerId, "Title one");

        mockMvc.perform(put(API + "/issues")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(new IssueDto(issueId, writerId, "Title two", "Updated content",
                                null, null))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Title two"));

        long commentId = createComment(issueId);
        mockMvc.perform(put(API + "/comments/{id}", commentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(new CommentDto(commentId, issueId, "updated comment"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("updated comment"));

        long tagId = createTag("java");
        mockMvc.perform(put(API + "/tags/{id}", tagId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(new TagDto(tagId, null, "spring"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("spring"));

        mockMvc.perform(get(API + "/issues"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
        mockMvc.perform(get(API + "/comments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
        mockMvc.perform(get(API + "/tags"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void duplicateAndInvalidAssociationErrors() throws Exception {
        long writerId = createWriter("writer4@mail.ru");
        createIssue(writerId, "Unique title");

        mockMvc.perform(post(API + "/writers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(new WriterDto(null, "writer4@mail.ru", "password123",
                                "Dmitry", "Loginov"))))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.errorCode").value("40301"));

        mockMvc.perform(post(API + "/issues")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(new IssueDto(null, writerId, "Unique title", "Issue content",
                                null, null))))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.errorCode").value("40302"));

        mockMvc.perform(post(API + "/issues")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(new IssueDto(null, 999_999L, "Another title", "Issue content",
                                null, null))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("40002"));

        mockMvc.perform(post(API + "/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(new CommentDto(null, 999_999L, "comment"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("40003"));
    }

    private long createWriter(String login) throws Exception {
        MvcResult result = mockMvc.perform(post(API + "/writers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(new WriterDto(null, login, "password123", "Dmitry", "Loginov"))))
                .andExpect(status().isCreated())
                .andReturn();
        return idFrom(result);
    }

    private long createIssue(long writerId, String title) throws Exception {
        MvcResult result = mockMvc.perform(post(API + "/issues")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(new IssueDto(null, writerId, title, "Issue content",
                                null, null))))
                .andExpect(status().isCreated())
                .andReturn();
        return idFrom(result);
    }

    private long createComment(long issueId) throws Exception {
        MvcResult result = mockMvc.perform(post(API + "/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(new CommentDto(null, issueId, "comment"))))
                .andExpect(status().isCreated())
                .andReturn();
        return idFrom(result);
    }

    private long createTag(String name) throws Exception {
        MvcResult result = mockMvc.perform(post(API + "/tags")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(new TagDto(null, null, name))))
                .andExpect(status().isCreated())
                .andReturn();
        return idFrom(result);
    }

    private String toJson(Object value) throws Exception {
        return objectMapper.writeValueAsString(value);
    }

    private long idFrom(MvcResult result) throws Exception {
        JsonNode node = objectMapper.readTree(result.getResponse().getContentAsString());
        return node.get("id").asLong();
    }
}
