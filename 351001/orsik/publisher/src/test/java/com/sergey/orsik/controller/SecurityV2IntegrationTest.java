package com.sergey.orsik.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sergey.orsik.dto.request.LabelRequestTo;
import com.sergey.orsik.dto.request.LoginRequestTo;
import com.sergey.orsik.dto.request.TweetRequestTo;
import com.sergey.orsik.dto.response.AuthTokenResponseTo;
import com.sergey.orsik.dto.response.LabelResponseTo;
import com.sergey.orsik.dto.response.TweetResponseTo;
import com.sergey.orsik.entity.Creator;
import com.sergey.orsik.entity.CreatorRole;
import com.sergey.orsik.repository.CreatorRepository;
import com.sergey.orsik.service.CommentService;
import com.sergey.orsik.service.LabelService;
import com.sergey.orsik.service.TweetService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityV2IntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CreatorRepository creatorRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockBean
    private TweetService tweetService;

    @MockBean
    private CommentService commentService;

    @MockBean
    private LabelService labelService;

    private Creator admin;
    private Creator customer;
    private Creator anotherCustomer;

    @BeforeEach
    void setUp() {
        creatorRepository.deleteAll();
        admin = creatorRepository.save(new Creator(
                null, "admin", passwordEncoder.encode("admin12345"), "Admin", "User", CreatorRole.ADMIN));
        customer = creatorRepository.save(new Creator(
                null, "customer", passwordEncoder.encode("customer12345"), "Customer", "User", CreatorRole.CUSTOMER));
        anotherCustomer = creatorRepository.save(new Creator(
                null, "other", passwordEncoder.encode("other12345"), "Other", "User", CreatorRole.CUSTOMER));
    }

    @Test
    void loginReturnsJwtToken() throws Exception {
        LoginRequestTo login = new LoginRequestTo("customer", "customer12345");
        mockMvc.perform(post("/api/v2.0/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.access_token").isNotEmpty())
                .andExpect(jsonPath("$.type_token").value("Bearer"));
    }

    @Test
    void loginWithInvalidCredentialsReturns401() throws Exception {
        LoginRequestTo login = new LoginRequestTo("nonexistent", "wrongpass");
        mockMvc.perform(post("/api/v2.0/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.errorCode").value("40101"));
    }

    @Test
    void protectedEndpointWithoutTokenReturns401WithErrorCode() throws Exception {
        mockMvc.perform(post("/api/v2.0/labels")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LabelRequestTo(null, "tech"))))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.errorCode").value("40101"))
                .andExpect(jsonPath("$.errorMessage").isNotEmpty());
    }

    @Test
    void customerCannotCreateLabelButAdminCan() throws Exception {
        String customerToken = loginAndGetToken("customer", "customer12345");

        mockMvc.perform(post("/api/v2.0/labels")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + customerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LabelRequestTo(null, "tech"))))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.errorCode").value("40301"));

        when(labelService.create(any(LabelRequestTo.class))).thenReturn(new LabelResponseTo(1L, "tech"));
        String adminToken = loginAndGetToken("admin", "admin12345");

        mockMvc.perform(post("/api/v2.0/labels")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LabelRequestTo(null, "tech"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void customerCanUpdateOnlyOwnTweet() throws Exception {
        String token = loginAndGetToken("customer", "customer12345");
        TweetRequestTo request = new TweetRequestTo(
                null,
                anotherCustomer.getId(),
                "title",
                "content",
                Instant.parse("2024-01-01T00:00:00Z"),
                Instant.parse("2024-01-01T00:00:00Z"),
                Set.of("java"));

        when(tweetService.findById(10L)).thenReturn(new TweetResponseTo(
                10L, customer.getId(), "t1", "c1", Instant.now(), Instant.now()));
        when(tweetService.update(eq(10L), any(TweetRequestTo.class))).thenReturn(new TweetResponseTo(
                10L, customer.getId(), "title", "content", Instant.now(), Instant.now()));

        mockMvc.perform(put("/api/v2.0/tweets/10")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10));

        when(tweetService.findById(11L)).thenReturn(new TweetResponseTo(
                11L, anotherCustomer.getId(), "t2", "c2", Instant.now(), Instant.now()));

        mockMvc.perform(put("/api/v2.0/tweets/11")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.errorCode").value("40301"));

        verify(tweetService, never()).update(eq(11L), any(TweetRequestTo.class));
    }

    private String loginAndGetToken(String login, String password) throws Exception {
        var result = mockMvc.perform(post("/api/v2.0/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginRequestTo(login, password))))
                .andExpect(status().isOk())
                .andReturn();
        AuthTokenResponseTo response = objectMapper.readValue(
                result.getResponse().getContentAsByteArray(),
                AuthTokenResponseTo.class);
        return response.getAccess_token();
    }
}
