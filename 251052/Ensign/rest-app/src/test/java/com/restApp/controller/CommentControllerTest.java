package com.restApp.controller;

import com.restApp.dto.AuthorRequestTo;
import com.restApp.dto.CommentRequestTo;
import com.restApp.dto.NewsRequestTo;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.util.Collections;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CommentControllerTest {

        @LocalServerPort
        private int port;

        private Long newsId;

        @BeforeEach
        void setUp() {
                RestAssured.port = port;

                // Setup Author and News
                AuthorRequestTo authorRequest = new AuthorRequestTo();
                authorRequest.setLogin("comment_tester_" + System.currentTimeMillis());
                authorRequest.setPassword("pass");
                authorRequest.setFirstname("Comment Author");
                authorRequest.setLastname("Test");

                Long authorId = given()
                                .contentType(ContentType.JSON)
                                .body(authorRequest)
                                .when()
                                .post("/api/v1.0/authors")
                                .then()
                                .statusCode(201)
                                .extract().jsonPath().getLong("id");

                NewsRequestTo newsRequest = new NewsRequestTo();
                newsRequest.setTitle("News for comments");
                newsRequest.setContent("Content");
                newsRequest.setAuthorId(authorId);
                newsRequest.setMarkIds(Collections.emptyList());

                newsId = given()
                                .contentType(ContentType.JSON)
                                .body(newsRequest)
                                .when()
                                .post("/api/v1.0/news")
                                .then()
                                .statusCode(201)
                                .extract().jsonPath().getLong("id");
        }

        @Test
        void testCreateAndGetComment() {
                CommentRequestTo request = new CommentRequestTo();
                request.setContent("Nice article!");
                request.setNewsId(newsId);
                request.setAuthorId(1L); // arbitrary/ignored for now based on logic

                Integer id = given()
                                .contentType(ContentType.JSON)
                                .body(request)
                                .when()
                                .post("/api/v1.0/comments")
                                .then()
                                .statusCode(201)
                                .body("content", equalTo("Nice article!"))
                                .extract().path("id");

                given()
                                .when()
                                .get("/api/v1.0/comments/" + id)
                                .then()
                                .statusCode(200)
                                .body("content", equalTo("Nice article!"));
        }
}
