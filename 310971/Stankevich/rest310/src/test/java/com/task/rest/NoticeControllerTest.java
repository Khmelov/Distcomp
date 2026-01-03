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
public class NoticeControllerTest {

    @LocalServerPort
    private int port;

    private Integer tweetId;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";
        RestAssured.basePath = "/api/v1.0";

        // Создаём автора
        String authorJson = """
                {
                    "login": "noticeauthor",
                    "password": "password123",
                    "firstname": "Notice",
                    "lastname": "Author"
                }
                """;

        Integer authorId = given()
                .contentType(ContentType.JSON)
                .body(authorJson)
                .when()
                .post("/authors")
                .then()
                .statusCode(201)
                .extract()
                .path("id");

        // Создаём твит
        String tweetJson = String.format("""
                {
                    "authorId": %d,
                    "title": "Tweet for Notice",
                    "content": "This tweet will have notices"
                }
                """, authorId);

        tweetId = given()
                .contentType(ContentType.JSON)
                .body(tweetJson)
                .when()
                .post("/tweets")
                .then()
                .statusCode(201)
                .extract()
                .path("id");
    }

    @Test
    void createNotice_shouldReturnCreatedStatus() {
        String noticeJson = String.format("""
                {
                    "tweetId": %d,
                    "content": "This is a valid notice"
                }
                """, tweetId);

        given()
                .contentType(ContentType.JSON)
                .body(noticeJson)
                .when()
                .post("/notices")
                .then()
                .statusCode(201)
                .body("content", equalTo("This is a valid notice"))
                .body("tweetId", equalTo(tweetId))
                .body("id", notNullValue());
    }

    @Test
    void getAllNotices_shouldReturnOk() {
        given()
                .when()
                .get("/notices")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON);
    }

    @Test
    void getNoticeById_shouldReturnOk_whenExists() {
        // Создаём комментарий
        String noticeJson = String.format("""
                {
                    "tweetId": %d,
                    "content": "Find this notice"
                }
                """, tweetId);

        Integer id = given()
                .contentType(ContentType.JSON)
                .body(noticeJson)
                .when()
                .post("/notices")
                .then()
                .statusCode(201)
                .extract()
                .path("id");

        // Получаем по ID
        given()
                .when()
                .get("/notices/" + id)
                .then()
                .statusCode(200)
                .body("id", equalTo(id))
                .body("content", equalTo("Find this notice"));
    }

    @Test
    void updateNotice_shouldReturnOk() {
        // Создаём комментарий
        String createJson = String.format("""
                {
                    "tweetId": %d,
                    "content": "Old content"
                }
                """, tweetId);

        Integer id = given()
                .contentType(ContentType.JSON)
                .body(createJson)
                .when()
                .post("/notices")
                .then()
                .statusCode(201)
                .extract()
                .path("id");

        // Обновляем
        String updateJson = String.format("""
                {
                    "id": %d,
                    "tweetId": %d,
                    "content": "New content"
                }
                """, id, tweetId);

        given()
                .contentType(ContentType.JSON)
                .body(updateJson)
                .when()
                .put("/notices")
                .then()
                .statusCode(200)
                .body("content", equalTo("New content"));
    }

    @Test
    void deleteNotice_shouldReturnNoContent() {
        // Создаём комментарий
        String noticeJson = String.format("""
                {
                    "tweetId": %d,
                    "content": "Delete this notice"
                }
                """, tweetId);

        Integer id = given()
                .contentType(ContentType.JSON)
                .body(noticeJson)
                .when()
                .post("/notices")
                .then()
                .statusCode(201)
                .extract()
                .path("id");

        // Удаляем
        given()
                .when()
                .delete("/notices/" + id)
                .then()
                .statusCode(204);

        // Проверяем что удалён
        given()
                .when()
                .get("/notices/" + id)
                .then()
                .statusCode(404);
    }

    @Test
    void createNotice_withShortContent_shouldReturnBadRequest() {
        String invalidJson = String.format("""
                {
                    "tweetId": %d,
                    "content": "a"
                }
                """, tweetId);

        given()
                .contentType(ContentType.JSON)
                .body(invalidJson)
                .when()
                .post("/notices")
                .then()
                .statusCode(400);
    }

    @Test
    void createNotice_withLongContent_shouldReturnBadRequest() {
        String invalidJson = String.format("""
                {
                    "tweetId": %d,
                    "content": "%s"
                }
                """, tweetId, "a".repeat(2049));

        given()
                .contentType(ContentType.JSON)
                .body(invalidJson)
                .when()
                .post("/notices")
                .then()
                .statusCode(400);
    }

    @Test
    void createNotice_withBlankContent_shouldReturnBadRequest() {
        String invalidJson = String.format("""
                {
                    "tweetId": %d,
                    "content": ""
                }
                """, tweetId);

        given()
                .contentType(ContentType.JSON)
                .body(invalidJson)
                .when()
                .post("/notices")
                .then()
                .statusCode(400);
    }

    @Test
    void createNotice_withNullTweetId_shouldReturnBadRequest() {
        String invalidJson = """
                {
                    "tweetId": null,
                    "content": "Valid content"
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(invalidJson)
                .when()
                .post("/notices")
                .then()
                .statusCode(400);
    }

    @Test
    void createNotice_withNonExistentTweetId_shouldReturnNotFound() {
        String invalidJson = """
                {
                    "tweetId": 99999,
                    "content": "Valid content"
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(invalidJson)
                .when()
                .post("/notices")
                .then()
                .statusCode(404);
    }

    @Test
    void getNotice_withNonExistentId_shouldReturnNotFound() {
        given()
                .when()
                .get("/notices/99999")
                .then()
                .statusCode(404);
    }
}