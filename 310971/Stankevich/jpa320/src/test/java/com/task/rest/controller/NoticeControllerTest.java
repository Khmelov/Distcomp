package com.task.rest.controller;

import com.task.rest.BaseIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

class NoticeControllerTest extends BaseIntegrationTest {

    private Integer authorId;
    private Integer tweetId;

    @BeforeEach
    void createAuthorAndTweet() {
        // Генерируем уникальный логин для каждого теста
        String uniqueLogin = "noticeauthor-" + UUID.randomUUID().toString().substring(0, 8);

        // Create author
        String authorBody = String.format("""
            {
                "login": "%s",
                "password": "password123",
                "firstname": "Notice",
                "lastname": "Author"
            }
            """, uniqueLogin);

        authorId = given()
                .spec(requestSpec)
                .body(authorBody)
                .when()
                .post("/api/v1.0/authors")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .path("id");

        // Create tweet with unique title
        String uniqueTitle = "Notice Tweet " + UUID.randomUUID().toString().substring(0, 8);
        String tweetBody = String.format("""
            {
                "authorId": %d,
                "title": "%s",
                "content": "Tweet for notices"
            }
            """, authorId, uniqueTitle);

        tweetId = given()
                .spec(requestSpec)
                .body(tweetBody)
                .when()
                .post("/api/v1.0/tweets")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .path("id");
    }

    @Test
    void testCreateNotice_Success() {
        String requestBody = String.format("""
            {
                "tweetId": %d,
                "content": "Test notice content"
            }
            """, tweetId);

        given()
                .spec(requestSpec)
                .body(requestBody)
                .when()
                .post("/api/v1.0/notices")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .body("content", equalTo("Test notice content"))
                .body("tweetId", equalTo(tweetId))
                .body("id", notNullValue())
                .body("created", notNullValue())
                .body("modified", notNullValue());
    }

    @Test
    void testCreateNotice_InvalidContent() {
        String requestBody = String.format("""
            {
                "tweetId": %d,
                "content": "x"
            }
            """, tweetId);

        given()
                .spec(requestSpec)
                .body(requestBody)
                .when()
                .post("/api/v1.0/notices")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("errorMessage", containsString("Content must be between 2 and 2048 characters"));
    }

    @Test
    void testGetNoticeById_Success() {
        String requestBody = String.format("""
            {
                "tweetId": %d,
                "content": "Get by ID notice"
            }
            """, tweetId);

        Integer noticeId = given()
                .spec(requestSpec)
                .body(requestBody)
                .when()
                .post("/api/v1.0/notices")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .path("id");

        given()
                .spec(requestSpec)
                .when()
                .get("/api/v1.0/notices/{id}", noticeId)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("id", equalTo(noticeId))
                .body("content", equalTo("Get by ID notice"));
    }

    @Test
    void testGetNoticeById_NotFound() {
        given()
                .spec(requestSpec)
                .when()
                .get("/api/v1.0/notices/{id}", 99999)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void testGetAllNotices_Success() {
        for (int i = 1; i <= 3; i++) {
            String requestBody = String.format("""
                {
                    "tweetId": %d,
                    "content": "Notice %d content"
                }
                """, tweetId, i);

            given()
                    .spec(requestSpec)
                    .body(requestBody)
                    .when()
                    .post("/api/v1.0/notices");
        }

        given()
                .spec(requestSpec)
                .when()
                .get("/api/v1.0/notices")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("size()", greaterThanOrEqualTo(3));
    }

    @Test
    void testUpdateNotice_Success() {
        String createBody = String.format("""
            {
                "tweetId": %d,
                "content": "Original notice"
            }
            """, tweetId);

        Integer noticeId = given()
                .spec(requestSpec)
                .body(createBody)
                .when()
                .post("/api/v1.0/notices")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .path("id");

        String updateBody = String.format("""
            {
                "tweetId": %d,
                "content": "Updated notice"
            }
            """, tweetId);

        given()
                .spec(requestSpec)
                .body(updateBody)
                .when()
                .put("/api/v1.0/notices/{id}", noticeId)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("content", equalTo("Updated notice"));
    }

    @Test
    void testDeleteNotice_Success() {
        String requestBody = String.format("""
            {
                "tweetId": %d,
                "content": "Delete this notice"
            }
            """, tweetId);

        Integer noticeId = given()
                .spec(requestSpec)
                .body(requestBody)
                .when()
                .post("/api/v1.0/notices")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .path("id");

        given()
                .spec(requestSpec)
                .when()
                .delete("/api/v1.0/notices/{id}", noticeId)
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());

        given()
                .spec(requestSpec)
                .when()
                .get("/api/v1.0/notices/{id}", noticeId)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }
}
