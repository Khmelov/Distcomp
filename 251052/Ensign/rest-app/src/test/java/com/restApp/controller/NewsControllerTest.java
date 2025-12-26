package com.restApp.controller;

import com.restApp.dto.NewsRequestTo;
import com.restApp.dto.AuthorRequestTo;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.util.Collections;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class NewsControllerTest {

    @LocalServerPort
    private int port;

    private Long authorId;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;

        // Create an author first
        AuthorRequestTo authorRequest = new AuthorRequestTo();
        authorRequest.setLogin("news_tester");
        authorRequest.setPassword("pass");
        authorRequest.setFirstname("News Author");
        authorRequest.setLastname("Test");

        // Try to delete if exists or create unique
        // For simplicity in test, assume clean state or ignore conflict
        // Or create with unique login
        authorRequest.setLogin("news_tester_" + System.currentTimeMillis());

        authorId = given()
                .contentType(ContentType.JSON)
                .body(authorRequest)
                .when()
                .post("/api/v1.0/authors")
                .then()
                .statusCode(201)
                .extract().jsonPath().getLong("id");
    }

    @Test
    void testCreateAndGetNews() {
        NewsRequestTo newsRequest = new NewsRequestTo();
        newsRequest.setTitle("Breaking News");
        newsRequest.setContent("Something happened");
        newsRequest.setAuthorId(authorId);
        newsRequest.setMarkIds(Collections.emptyList());

        Integer newsId = given()
                .contentType(ContentType.JSON)
                .body(newsRequest)
                .when()
                .post("/api/v1.0/news")
                .then()
                .statusCode(201)
                .body("title", equalTo("Breaking News"))
                .extract().path("id");

        given()
                .when()
                .get("/api/v1.0/news/" + newsId)
                .then()
                .statusCode(200)
                .body("title", equalTo("Breaking News"))
                .body("content", equalTo("Something happened"));
    }
}
