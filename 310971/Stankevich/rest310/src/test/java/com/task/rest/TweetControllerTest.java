package com.task.rest;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TweetControllerTest {

    @LocalServerPort
    private int port;

    private Integer authorId;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";
        RestAssured.basePath = "/api/v1.0";

        // Создаём автора для твитов
        String authorJson = """
                {
                    "login": "tweetauthor",
                    "password": "password123",
                    "firstname": "Tweet",
                    "lastname": "Author"
                }
                """;

        authorId = given()
                .contentType(ContentType.JSON)
                .body(authorJson)
                .when()
                .post("/authors")
                .then()
                .statusCode(201)
                .extract()
                .path("id");
    }

    @Test
    void createTweet_shouldReturnCreatedStatus() {
        String tweetJson = String.format("""
                {
                    "authorId": %d,
                    "title": "Test Tweet",
                    "content": "This is a test tweet content"
                }
                """, authorId);

        given()
                .contentType(ContentType.JSON)
                .body(tweetJson)
                .when()
                .post("/tweets")
                .then()
                .statusCode(201)
                .body("title", equalTo("Test Tweet"))
                .body("content", equalTo("This is a test tweet content"))
                .body("authorId", equalTo(authorId))
                .body("id", notNullValue())
                .body("created", notNullValue())
                .body("modified", notNullValue());
    }

    @Test
    void getAllTweets_shouldReturnOk() {
        given()
                .when()
                .get("/tweets")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON);
    }

    @Test
    void getTweetById_shouldReturnOk_whenExists() {
        // Создаём твит
        String tweetJson = String.format("""
                {
                    "authorId": %d,
                    "title": "Find Me",
                    "content": "This tweet should be found"
                }
                """, authorId);

        Integer id = given()
                .contentType(ContentType.JSON)
                .body(tweetJson)
                .when()
                .post("/tweets")
                .then()
                .statusCode(201)
                .extract()
                .path("id");

        // Получаем по ID
        given()
                .when()
                .get("/tweets/" + id)
                .then()
                .statusCode(200)
                .body("id", equalTo(id))
                .body("title", equalTo("Find Me"));
    }

    @Test
    void updateTweet_shouldReturnOk() {
        // Создаём твит
        String createJson = String.format("""
                {
                    "authorId": %d,
                    "title": "Old Title",
                    "content": "Old content here"
                }
                """, authorId);

        Integer id = given()
                .contentType(ContentType.JSON)
                .body(createJson)
                .when()
                .post("/tweets")
                .then()
                .statusCode(201)
                .extract()
                .path("id");

        // Обновляем
        String updateJson = String.format("""
                {
                    "id": %d,
                    "authorId": %d,
                    "title": "New Title",
                    "content": "New content here"
                }
                """, id, authorId);

        given()
                .contentType(ContentType.JSON)
                .body(updateJson)
                .when()
                .put("/tweets")
                .then()
                .statusCode(200)
                .body("title", equalTo("New Title"))
                .body("content", equalTo("New content here"));
    }

    @Test
    void deleteTweet_shouldReturnNoContent() {
        // Создаём твит
        String tweetJson = String.format("""
                {
                    "authorId": %d,
                    "title": "Delete Me",
                    "content": "This will be deleted"
                }
                """, authorId);

        Integer id = given()
                .contentType(ContentType.JSON)
                .body(tweetJson)
                .when()
                .post("/tweets")
                .then()
                .statusCode(201)
                .extract()
                .path("id");

        // Удаляем
        given()
                .when()
                .delete("/tweets/" + id)
                .then()
                .statusCode(204);

        // Проверяем что удалён
        given()
                .when()
                .get("/tweets/" + id)
                .then()
                .statusCode(404);
    }

    @Test
    void createTweet_withShortTitle_shouldReturnBadRequest() {
        String invalidJson = String.format("""
                {
                    "authorId": %d,
                    "title": "a",
                    "content": "Valid content"
                }
                """, authorId);

        given()
                .contentType(ContentType.JSON)
                .body(invalidJson)
                .when()
                .post("/tweets")
                .then()
                .statusCode(400);
    }

    @Test
    void createTweet_withShortContent_shouldReturnBadRequest() {
        String invalidJson = String.format("""
                {
                    "authorId": %d,
                    "title": "Valid Title",
                    "content": "abc"
                }
                """, authorId);

        given()
                .contentType(ContentType.JSON)
                .body(invalidJson)
                .when()
                .post("/tweets")
                .then()
                .statusCode(400);
    }

    @Test
    void createTweet_withNullAuthorId_shouldReturnBadRequest() {
        String invalidJson = """
                {
                    "authorId": null,
                    "title": "Valid Title",
                    "content": "Valid content"
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(invalidJson)
                .when()
                .post("/tweets")
                .then()
                .statusCode(400);
    }

    @Test
    void createTweet_withNonExistentAuthorId_shouldReturnNotFound() {
        String invalidJson = """
                {
                    "authorId": 99999,
                    "title": "Valid Title",
                    "content": "Valid content"
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(invalidJson)
                .when()
                .post("/tweets")
                .then()
                .statusCode(404);
    }

    @Test
    void getTweet_withNonExistentId_shouldReturnNotFound() {
        given()
                .when()
                .get("/tweets/99999")
                .then()
                .statusCode(404);
    }

    @Test
    void searchTweets_byAuthorLogin_shouldReturnOk() {
        // Создаём твит
        String tweetJson = String.format("""
                {
                    "authorId": %d,
                    "title": "Searchable",
                    "content": "Content for search"
                }
                """, authorId);

        given()
                .contentType(ContentType.JSON)
                .body(tweetJson)
                .post("/tweets");

        // Ищем по логину автора
        given()
                .queryParam("authorLogin", "tweetauthor")
                .when()
                .get("/tweets")
                .then()
                .statusCode(200)
                .body("size()", greaterThan(0));
    }

    @Test
    void searchTweets_byTitle_shouldReturnOk() {
        given()
                .queryParam("title", "Test")
                .when()
                .get("/tweets")
                .then()
                .statusCode(200);
    }

    @Test
    void searchTweets_byContent_shouldReturnOk() {
        given()
                .queryParam("content", "content")
                .when()
                .get("/tweets")
                .then()
                .statusCode(200);
    }

    @Test
    void addMarkToTweet_shouldReturnOk() {
        // Создаём твит
        String tweetJson = String.format("""
                {
                    "authorId": %d,
                    "title": "Tweet with Mark",
                    "content": "This tweet will have a mark"
                }
                """, authorId);

        Integer tweetId = given()
                .contentType(ContentType.JSON)
                .body(tweetJson)
                .post("/tweets")
                .then()
                .statusCode(201)
                .extract()
                .path("id");

        // Создаём метку
        String markJson = """
                {
                    "name": "TestMark"
                }
                """;

        Integer markId = given()
                .contentType(ContentType.JSON)
                .body(markJson)
                .post("/marks")
                .then()
                .statusCode(201)
                .extract()
                .path("id");

        // Добавляем метку к твиту
        given()
                .when()
                .post("/tweets/" + tweetId + "/marks/" + markId)
                .then()
                .statusCode(200);
    }

    @Test
    void removeMarkFromTweet_shouldReturnNoContent() {
        // Создаём твит
        String tweetJson = String.format("""
                {
                    "authorId": %d,
                    "title": "Tweet for removal",
                    "content": "Mark will be removed"
                }
                """, authorId);

        Integer tweetId = given()
                .contentType(ContentType.JSON)
                .body(tweetJson)
                .post("/tweets")
                .then()
                .extract()
                .path("id");

        // Создаём метку
        String markJson = """
                {
                    "name": "RemoveMark"
                }
                """;

        Integer markId = given()
                .contentType(ContentType.JSON)
                .body(markJson)
                .post("/marks")
                .then()
                .extract()
                .path("id");

        // Добавляем
        given().post("/tweets/" + tweetId + "/marks/" + markId);

        // Удаляем
        given()
                .when()
                .delete("/tweets/" + tweetId + "/marks/" + markId)
                .then()
                .statusCode(204);
    }
}