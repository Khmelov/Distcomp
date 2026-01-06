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
public class MessageControllerTest {

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";
    }

    private Long createTweet() {
        // Create writer first
        String writerBody = """
                {
                    "login": "messagetest@example.com",
                    "password": "password123",
                    "firstname": "Message",
                    "lastname": "Writer"
                }
                """;

        Long writerId = given()
                .contentType(ContentType.JSON)
                .body(writerBody)
                .when()
                .post("/api/v1/writers")
                .then()
                .extract()
                .path("id");

        // Create tweet
        String tweetBody = String.format("""
                {
                    "writerId": %d,
                    "title": "Test Tweet",
                    "content": "Tweet content for message test"
                }
                """, writerId);

        return given()
                .contentType(ContentType.JSON)
                .body(tweetBody)
                .when()
                .post("/api/v1/tweets")
                .then()
                .extract()
                .path("id");
    }

    @Test
    void testCreateMessage() {
        Long tweetId = createTweet();

        String requestBody = String.format("""
                {
                    "tweetId": %d,
                    "content": "This is a test message"
                }
                """, tweetId);

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/v1/messages")
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("content", equalTo("This is a test message"))
                .body("tweetId", equalTo(tweetId.intValue()));
    }

    @Test
    void testGetAllMessages() {
        given()
                .when()
                .get("/api/v1/messages")
                .then()
                .statusCode(200)
                .body("$", instanceOf(java.util.List.class));
    }

    @Test
    void testGetMessageById() {
        Long tweetId = createTweet();

        String createBody = String.format("""
                {
                    "tweetId": %d,
                    "content": "Get test message"
                }
                """, tweetId);

        Long id = given()
                .contentType(ContentType.JSON)
                .body(createBody)
                .when()
                .post("/api/v1/messages")
                .then()
                .extract()
                .path("id");

        given()
                .when()
                .get("/api/v1/messages/" + id)
                .then()
                .statusCode(200)
                .body("id", equalTo(id.intValue()))
                .body("content", equalTo("Get test message"));
    }

    @Test
    void testUpdateMessage() {
        Long tweetId = createTweet();

        String createBody = String.format("""
                {
                    "tweetId": %d,
                    "content": "Original message"
                }
                """, tweetId);

        Long id = given()
                .contentType(ContentType.JSON)
                .body(createBody)
                .when()
                .post("/api/v1/messages")
                .then()
                .extract()
                .path("id");

        String updateBody = String.format("""
                {
                    "tweetId": %d,
                    "content": "Updated message"
                }
                """, tweetId);

        given()
                .contentType(ContentType.JSON)
                .body(updateBody)
                .when()
                .put("/api/v1/messages/" + id)
                .then()
                .statusCode(200)
                .body("id", equalTo(id.intValue()))
                .body("content", equalTo("Updated message"));
    }

    @Test
    void testDeleteMessage() {
        Long tweetId = createTweet();

        String createBody = String.format("""
                {
                    "tweetId": %d,
                    "content": "Message to delete"
                }
                """, tweetId);

        Long id = given()
                .contentType(ContentType.JSON)
                .body(createBody)
                .when()
                .post("/api/v1/messages")
                .then()
                .extract()
                .path("id");

        given()
                .when()
                .delete("/api/v1/messages/" + id)
                .then()
                .statusCode(204);

        given()
                .when()
                .get("/api/v1/messages/" + id)
                .then()
                .statusCode(404);
    }
}

