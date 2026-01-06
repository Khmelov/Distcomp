package com.labs.controller;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class TweetControllerTest {

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";
    }

    private Long createWriter() {
        String writerBody = """
                {
                    "login": "tweettest@example.com",
                    "password": "password123",
                    "firstname": "Tweet",
                    "lastname": "Writer"
                }
                """;

        return given()
                .contentType(ContentType.JSON)
                .body(writerBody)
                .when()
                .post("/api/v1/writers")
                .then()
                .extract()
                .path("id");
    }

    @Test
    void testCreateTweet() {
        Long writerId = createWriter();

        String requestBody = String.format("""
                {
                    "writerId": %d,
                    "title": "Test Tweet",
                    "content": "This is a test tweet content"
                }
                """, writerId);

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/v1/tweets")
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("title", equalTo("Test Tweet"))
                .body("content", equalTo("This is a test tweet content"))
                .body("writerId", equalTo(writerId.intValue()));
    }

    @Test
    void testGetAllTweets() {
        given()
                .when()
                .get("/api/v1/tweets")
                .then()
                .statusCode(200)
                .body("$", instanceOf(java.util.List.class));
    }

    @Test
    void testGetTweetById() {
        Long writerId = createWriter();

        String createBody = String.format("""
                {
                    "writerId": %d,
                    "title": "Get Test Tweet",
                    "content": "Content for get test"
                }
                """, writerId);

        Long id = given()
                .contentType(ContentType.JSON)
                .body(createBody)
                .when()
                .post("/api/v1/tweets")
                .then()
                .extract()
                .path("id");

        given()
                .when()
                .get("/api/v1/tweets/" + id)
                .then()
                .statusCode(200)
                .body("id", equalTo(id.intValue()))
                .body("title", equalTo("Get Test Tweet"));
    }

    @Test
    void testUpdateTweet() {
        Long writerId = createWriter();

        String createBody = String.format("""
                {
                    "writerId": %d,
                    "title": "Original Title",
                    "content": "Original content"
                }
                """, writerId);

        Long id = given()
                .contentType(ContentType.JSON)
                .body(createBody)
                .when()
                .post("/api/v1/tweets")
                .then()
                .extract()
                .path("id");

        String updateBody = String.format("""
                {
                    "writerId": %d,
                    "title": "Updated Title",
                    "content": "Updated content"
                }
                """, writerId);

        given()
                .contentType(ContentType.JSON)
                .body(updateBody)
                .when()
                .put("/api/v1/tweets/" + id)
                .then()
                .statusCode(200)
                .body("id", equalTo(id.intValue()))
                .body("title", equalTo("Updated Title"));
    }

    @Test
    void testDeleteTweet() {
        Long writerId = createWriter();

        String createBody = String.format("""
                {
                    "writerId": %d,
                    "title": "Delete Test",
                    "content": "Content to delete"
                }
                """, writerId);

        Long id = given()
                .contentType(ContentType.JSON)
                .body(createBody)
                .when()
                .post("/api/v1/tweets")
                .then()
                .extract()
                .path("id");

        given()
                .when()
                .delete("/api/v1/tweets/" + id)
                .then()
                .statusCode(204);

        given()
                .when()
                .get("/api/v1/tweets/" + id)
                .then()
                .statusCode(404);
    }

    @Test
    void testGetWriterByTweetId() {
        Long writerId = createWriter();

        String createBody = String.format("""
                {
                    "writerId": %d,
                    "title": "Test",
                    "content": "Content"
                }
                """, writerId);

        Long tweetId = given()
                .contentType(ContentType.JSON)
                .body(createBody)
                .when()
                .post("/api/v1/tweets")
                .then()
                .extract()
                .path("id");

        given()
                .when()
                .get("/api/v1/tweets/" + tweetId + "/writer")
                .then()
                .statusCode(200)
                .body("id", equalTo(writerId.intValue()));
    }

    @Test
    void testGetLabelsByTweetId() {
        Long writerId = createWriter();

        String createBody = String.format("""
                {
                    "writerId": %d,
                    "title": "Test",
                    "content": "Content"
                }
                """, writerId);

        Long tweetId = given()
                .contentType(ContentType.JSON)
                .body(createBody)
                .when()
                .post("/api/v1/tweets")
                .then()
                .extract()
                .path("id");

        given()
                .when()
                .get("/api/v1/tweets/" + tweetId + "/labels")
                .then()
                .statusCode(200)
                .body("$", instanceOf(java.util.List.class));
    }

    @Test
    void testGetMessagesByTweetId() {
        Long writerId = createWriter();

        String createBody = String.format("""
                {
                    "writerId": %d,
                    "title": "Test",
                    "content": "Content"
                }
                """, writerId);

        Long tweetId = given()
                .contentType(ContentType.JSON)
                .body(createBody)
                .when()
                .post("/api/v1/tweets")
                .then()
                .extract()
                .path("id");

        given()
                .when()
                .get("/api/v1/tweets/" + tweetId + "/messages")
                .then()
                .statusCode(200)
                .body("$", instanceOf(java.util.List.class));
    }
}

