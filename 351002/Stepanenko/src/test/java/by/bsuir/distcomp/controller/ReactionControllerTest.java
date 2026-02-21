package by.bsuir.distcomp.controller;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ReactionControllerTest {
    @LocalServerPort
    private int port;

    private Long tweetId;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";

        String authorBody = """
                {
                    "login": "reactionauthor",
                    "password": "password123",
                    "firstname": "Reaction",
                    "lastname": "Author"
                }
                """;

        Long authorId = given()
                .contentType(ContentType.JSON)
                .body(authorBody)
        .when()
                .post("/api/v1.0/authors")
        .then()
                .statusCode(201)
                .extract()
                .path("id");

        String tweetBody = """
                {
                    "authorId": %d,
                    "title": "Reaction Test Tweet",
                    "content": "Content for reaction test"
                }
                """.formatted(authorId);

        tweetId = given()
                .contentType(ContentType.JSON)
                .body(tweetBody)
        .when()
                .post("/api/v1.0/tweets")
        .then()
                .statusCode(201)
                .extract()
                .path("id");
    }

    @Test
    void testCreateReaction() {
        String requestBody = """
                {
                    "tweetId": %d,
                    "content": "This is a reaction"
                }
                """.formatted(tweetId);

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
        .when()
                .post("/api/v1.0/reactions")
        .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("tweetId", equalTo(tweetId.intValue()))
                .body("content", equalTo("This is a reaction"));
    }

    @Test
    void testGetReactionById() {
        String createBody = """
                {
                    "tweetId": %d,
                    "content": "Get reaction content"
                }
                """.formatted(tweetId);

        Long id = given()
                .contentType(ContentType.JSON)
                .body(createBody)
        .when()
                .post("/api/v1.0/reactions")
        .then()
                .statusCode(201)
                .extract()
                .path("id");

        given()
        .when()
                .get("/api/v1.0/reactions/" + id)
        .then()
                .statusCode(200)
                .body("id", equalTo(id.intValue()))
                .body("content", equalTo("Get reaction content"));
    }

    @Test
    void testGetAllReactions() {
        given()
        .when()
                .get("/api/v1.0/reactions")
        .then()
                .statusCode(200)
                .body("$", instanceOf(java.util.List.class));
    }

    @Test
    void testUpdateReaction() {
        String createBody = """
                {
                    "tweetId": %d,
                    "content": "Original reaction"
                }
                """.formatted(tweetId);

        Long id = given()
                .contentType(ContentType.JSON)
                .body(createBody)
        .when()
                .post("/api/v1.0/reactions")
        .then()
                .statusCode(201)
                .extract()
                .path("id");

        String updateBody = """
                {
                    "id": %d,
                    "tweetId": %d,
                    "content": "Updated reaction"
                }
                """.formatted(id, tweetId);

        given()
                .contentType(ContentType.JSON)
                .body(updateBody)
        .when()
                .put("/api/v1.0/reactions")
        .then()
                .statusCode(200)
                .body("id", equalTo(id.intValue()))
                .body("content", equalTo("Updated reaction"));
    }

    @Test
    void testDeleteReaction() {
        String createBody = """
                {
                    "tweetId": %d,
                    "content": "Delete reaction"
                }
                """.formatted(tweetId);

        Long id = given()
                .contentType(ContentType.JSON)
                .body(createBody)
        .when()
                .post("/api/v1.0/reactions")
        .then()
                .statusCode(201)
                .extract()
                .path("id");

        given()
        .when()
                .delete("/api/v1.0/reactions/" + id)
        .then()
                .statusCode(204);

        given()
        .when()
                .get("/api/v1.0/reactions/" + id)
        .then()
                .statusCode(404);
    }

    @Test
    void testGetReactionsByTweetId() {
        String createBody = """
                {
                    "tweetId": %d,
                    "content": "Reaction for tweet"
                }
                """.formatted(tweetId);

        given()
                .contentType(ContentType.JSON)
                .body(createBody)
        .when()
                .post("/api/v1.0/reactions")
        .then()
                .statusCode(201);

        given()
        .when()
                .get("/api/v1.0/reactions/tweet/" + tweetId)
        .then()
                .statusCode(200)
                .body("$", instanceOf(java.util.List.class))
                .body("size()", greaterThan(0));
    }

    @Test
    void testCreateReactionValidation() {
        String requestBody = """
                {
                    "tweetId": %d,
                    "content": ""
                }
                """.formatted(tweetId);

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
        .when()
                .post("/api/v1.0/reactions")
        .then()
                .statusCode(400);
    }

    @Test
    void testGetReactionNotFound() {
        given()
        .when()
                .get("/api/v1.0/reactions/99999")
        .then()
                .statusCode(404);
    }
}
