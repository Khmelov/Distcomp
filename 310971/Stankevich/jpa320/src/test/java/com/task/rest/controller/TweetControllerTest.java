package com.task.rest.controller;

import com.task.rest.BaseIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

class TweetControllerTest extends BaseIntegrationTest {

    private Integer authorId;

    @BeforeEach
    void createAuthor() {
        // Генерируем уникальный логин для каждого теста
        String uniqueLogin = "tweetauthor-" + UUID.randomUUID().toString().substring(0, 8);

        String requestBody = String.format("""
            {
                "login": "%s",
                "password": "password123",
                "firstname": "Tweet",
                "lastname": "Author"
            }
            """, uniqueLogin);

        authorId = given()
                .spec(requestSpec)
                .body(requestBody)
                .when()
                .post("/api/v1.0/authors")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .path("id");
    }

    @Test
    void testCreateTweet_Success() {
        String requestBody = String.format("""
            {
                "authorId": %d,
                "title": "Test Tweet",
                "content": "This is a test tweet content",
                "created": "2024-01-01T10:00:00",
                "modified": "2024-01-01T10:00:00",
                "marks": ["tag1", "tag2", "tag3"]
            }
            """, authorId);

        given()
                .spec(requestSpec)
                .body(requestBody)
                .when()
                .post("/api/v1.0/tweets")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .body("title", equalTo("Test Tweet"))
                .body("content", equalTo("This is a test tweet content"))
                .body("authorId", equalTo(authorId))
                .body("marks", hasSize(3))
                .body("id", notNullValue());
    }

    @Test
    void testCreateTweet_WithMarks() {
        String requestBody = String.format("""
            {
                "authorId": %d,
                "title": "Tweet with Marks",
                "content": "Content with marks",
                "created": "2024-01-01T10:00:00",
                "modified": "2024-01-01T10:00:00",
                "marks": ["java", "spring", "testing"]
            }
            """, authorId);

        given()
                .spec(requestSpec)
                .body(requestBody)
                .when()
                .post("/api/v1.0/tweets")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .body("marks", hasSize(3))
                .body("marks", hasItems("java", "spring", "testing"));
    }

    @Test
    void testCreateTweet_InvalidTitle() {
        String requestBody = String.format("""
            {
                "authorId": %d,
                "title": "x",
                "content": "Valid content",
                "created": "2024-01-01T10:00:00",
                "modified": "2024-01-01T10:00:00"
            }
            """, authorId);

        given()
                .spec(requestSpec)
                .body(requestBody)
                .when()
                .post("/api/v1.0/tweets")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("errorMessage", containsString("Title must be between 2 and 64 characters"));
    }

    @Test
    void testCreateTweet_DuplicateTitle() {
        String uniqueTitle = "Duplicate Title " + UUID.randomUUID().toString().substring(0, 8);
        String requestBody = String.format("""
            {
                "authorId": %d,
                "title": "%s",
                "content": "First tweet",
                "created": "2024-01-01T10:00:00",
                "modified": "2024-01-01T10:00:00"
            }
            """, authorId, uniqueTitle);

        // Create first tweet
        given()
                .spec(requestSpec)
                .body(requestBody)
                .when()
                .post("/api/v1.0/tweets")
                .then()
                .statusCode(HttpStatus.CREATED.value());

        // Try to create duplicate
        String duplicateRequestBody = String.format("""
            {
                "authorId": %d,
                "title": "%s",
                "content": "Second tweet with same title",
                "created": "2024-01-01T11:00:00",
                "modified": "2024-01-01T11:00:00"
            }
            """, authorId, uniqueTitle);

        given()
                .spec(requestSpec)
                .body(duplicateRequestBody)
                .when()
                .post("/api/v1.0/tweets")
                .then()
                .statusCode(HttpStatus.FORBIDDEN.value())
                .body("errorMessage", containsString("Tweet with title already exists"));
    }

    @Test
    void testCreateTweet_ContentAsNumber() {
        String requestBody = String.format("""
            {
                "authorId": %d,
                "title": "Numeric Content",
                "content": "1",
                "created": "2024-01-01T10:00:00",
                "modified": "2024-01-01T10:00:00"
            }
            """, authorId);

        given()
                .spec(requestSpec)
                .body(requestBody)
                .when()
                .post("/api/v1.0/tweets")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("errorMessage", containsString("Content must be between 4 and 2048 characters"));
    }

    @Test
    void testGetTweetById_Success() {
        String requestBody = String.format("""
            {
                "authorId": %d,
                "title": "Get Tweet Test",
                "content": "Content for get test",
                "created": "2024-01-01T10:00:00",
                "modified": "2024-01-01T10:00:00"
            }
            """, authorId);

        Integer tweetId = given()
                .spec(requestSpec)
                .body(requestBody)
                .when()
                .post("/api/v1.0/tweets")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .path("id");

        given()
                .spec(requestSpec)
                .when()
                .get("/api/v1.0/tweets/{id}", tweetId)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("id", equalTo(tweetId))
                .body("title", equalTo("Get Tweet Test"));
    }

    @Test
    void testGetTweetById_NotFound() {
        given()
                .spec(requestSpec)
                .when()
                .get("/api/v1.0/tweets/{id}", 99999)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void testGetAllTweets_Success() {
        for (int i = 1; i <= 3; i++) {
            String requestBody = String.format("""
                {
                    "authorId": %d,
                    "title": "Tweet %d",
                    "content": "Content %d",
                    "created": "2024-01-01T10:00:00",
                    "modified": "2024-01-01T10:00:00"
                }
                """, authorId, i, i);

            given()
                    .spec(requestSpec)
                    .body(requestBody)
                    .when()
                    .post("/api/v1.0/tweets");
        }

        given()
                .spec(requestSpec)
                .when()
                .get("/api/v1.0/tweets")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("size()", greaterThanOrEqualTo(3));
    }

    @Test
    void testUpdateTweet_Success() {
        String createBody = String.format("""
            {
                "authorId": %d,
                "title": "Original Title",
                "content": "Original content",
                "created": "2024-01-01T10:00:00",
                "modified": "2024-01-01T10:00:00"
            }
            """, authorId);

        Integer tweetId = given()
                .spec(requestSpec)
                .body(createBody)
                .when()
                .post("/api/v1.0/tweets")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .path("id");

        String updateBody = String.format("""
            {
                "authorId": %d,
                "title": "Updated Title",
                "content": "Updated content",
                "created": "2024-01-01T10:00:00",
                "modified": "2024-01-01T11:00:00"
            }
            """, authorId);

        given()
                .spec(requestSpec)
                .body(updateBody)
                .when()
                .put("/api/v1.0/tweets/{id}", tweetId)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("title", equalTo("Updated Title"))
                .body("content", equalTo("Updated content"));
    }

    @Test
    void testDeleteTweet_Success() {
        String requestBody = String.format("""
            {
                "authorId": %d,
                "title": "Delete Test Tweet",
                "content": "This tweet will be deleted",
                "created": "2024-01-01T10:00:00",
                "modified": "2024-01-01T10:00:00"
            }
            """, authorId);

        Integer tweetId = given()
                .spec(requestSpec)
                .body(requestBody)
                .when()
                .post("/api/v1.0/tweets")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .path("id");

        given()
                .spec(requestSpec)
                .when()
                .delete("/api/v1.0/tweets/{id}", tweetId)
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());

        given()
                .spec(requestSpec)
                .when()
                .get("/api/v1.0/tweets/{id}", tweetId)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }
}
