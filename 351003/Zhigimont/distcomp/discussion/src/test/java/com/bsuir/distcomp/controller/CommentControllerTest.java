package com.bsuir.distcomp.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;

import static org.junit.jupiter.api.Assertions.assertTrue;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CommentControllerTest {

    @Autowired
    private TestRestTemplate rest;

    @Test
    void create_shouldReturn200() {
        var response = rest.postForEntity(
                "/api/v1.0/comments?topicId=1&writerId=10&content=test",
                null,
                String.class
        );

        assertTrue(response.getStatusCode().is2xxSuccessful());
    }
}
