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
class TweetControllerTest {
    @LocalServerPort
    private int port;

    private Long authorId;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";

        String authorBody = """
                {
                    "login": "tweetauthor",
                    "password": "password123",
                    "firstname": "Tweet",
                    "lastname": "Author"
                }
                """;

        authorId = given()
                .contentType(ContentType.JSON)
                .body(authorBody)
        .when()
                .post("/api/v1.0/authors")
        .then()
                .statusCode(201)
                .extract()
                .path("id");
    }

    @Test
    void testCreateTweet() {
        String requestBody = """
                {
                    "authorId": %d,
                    "title": "Test Tweet",
                    "content": "This is a test tweet content"
                }
                """.formatted(authorId);

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
        .when()
                .post("/api/v1.0/tweets")
        .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("authorId", equalTo(authorId.intValue()))
                .body("title", equalTo("Test Tweet"))
                .body("content", equalTo("This is a test tweet content"))
                .body("created", notNullValue())
                .body("modified", notNullValue());
    }

    @Test
    void testGetTweetById() {
        String createBody = """
                {
                    "authorId": %d,
                    "title": "Get Tweet",
                    "content": "Content for get test"
                }
                """.formatted(authorId);

        Long id = given()
                .contentType(ContentType.JSON)
                .body(createBody)
        .when()
                .post("/api/v1.0/tweets")
        .then()
                .statusCode(201)
                .extract()
                .path("id");

        given()
        .when()
                .get("/api/v1.0/tweets/" + id)
        .then()
                .statusCode(200)
                .body("id", equalTo(id.intValue()))
                .body("title", equalTo("Get Tweet"));
    }

    @Test
    void testGetAllTweets() {
        given()
        .when()
                .get("/api/v1.0/tweets")
        .then()
                .statusCode(200)
                .body("$", instanceOf(java.util.List.class));
    }

    @Test
    void testUpdateTweet() {
        String createBody = """
                {
                    "authorId": %d,
                    "title": "Update Tweet",
                    "content": "Original content"
                }
                """.formatted(authorId);

        Long id = given()
                .contentType(ContentType.JSON)
                .body(createBody)
        .when()
                .post("/api/v1.0/tweets")
        .then()
                .statusCode(201)
                .extract()
                .path("id");

        String updateBody = """
                {
                    "id": %d,
                    "authorId": %d,
                    "title": "Updated Tweet",
                    "content": "Updated content"
                }
                """.formatted(id, authorId);

        given()
                .contentType(ContentType.JSON)
                .body(updateBody)
        .when()
                .put("/api/v1.0/tweets")
        .then()
                .statusCode(200)
                .body("id", equalTo(id.intValue()))
                .body("title", equalTo("Updated Tweet"))
                .body("content", equalTo("Updated content"));
    }

    @Test
    void testDeleteTweet() {
        String createBody = """
                {
                    "authorId": %d,
                    "title": "Delete Tweet",
                    "content": "Content to delete"
                }
                """.formatted(authorId);

        Long id = given()
                .contentType(ContentType.JSON)
                .body(createBody)
        .when()
                .post("/api/v1.0/tweets")
        .then()
                .statusCode(201)
                .extract()
                .path("id");

        given()
        .when()
                .delete("/api/v1.0/tweets/" + id)
        .then()
                .statusCode(204);

        given()
        .when()
                .get("/api/v1.0/tweets/" + id)
        .then()
                .statusCode(404);
    }

    @Test
    void testGetAuthorByTweetId() {
        String createBody = """
                {
                    "authorId": %d,
                    "title": "Author Test Tweet",
                    "content": "Content"
                }
                """.formatted(authorId);

        Long tweetId = given()
                .contentType(ContentType.JSON)
                .body(createBody)
        .when()
                .post("/api/v1.0/tweets")
        .then()
                .statusCode(201)
                .extract()
                .path("id");

        given()
        .when()
                .get("/api/v1.0/tweets/" + tweetId + "/author")
        .then()
                .statusCode(200)
                .body("id", equalTo(authorId.intValue()))
                .body("login", equalTo("tweetauthor"));
    }

    @Test
    void testCreateTweetValidation() {
        String requestBody = """
                {
                    "authorId": %d,
                    "title": "",
                    "content": "x"
                }
                """.formatted(authorId);

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
        .when()
                .post("/api/v1.0/tweets")
        .then()
                .statusCode(400);
    }

    @Test
    void testGetTweetNotFound() {
        given()
        .when()
                .get("/api/v1.0/tweets/99999")
        .then()
                .statusCode(404);
    }
}
