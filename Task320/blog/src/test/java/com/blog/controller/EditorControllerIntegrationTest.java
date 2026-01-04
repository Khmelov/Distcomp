package com.blog.controller;

import com.blog.AbstractIntegrationTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.web.server.LocalServerPort;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

class EditorControllerTest extends AbstractIntegrationTest {

    @LocalServerPort
    private int port;

    @Test
    void testCreateAndGetEditor() {
        // Создаем редактора
        String editorJson = """
            {
                "login": "test@example.com",
                "password": "password123",
                "firstname": "Иван",
                "lastname": "Иванов"
            }
            """;

        String location = given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(editorJson)
                .when()
                .post("/api/v1.0/editors")
                .then()
                .statusCode(201)
                .extract()
                .header("Location");

        // Получаем ID из location header
        String[] parts = location.split("/");
        Long editorId = Long.parseLong(parts[parts.length - 1]);

        // Получаем редактора по ID
        given()
                .port(port)
                .when()
                .get("/api/v1.0/editors/{id}", editorId)
                .then()
                .statusCode(200)
                .body("id", equalTo(editorId.intValue()))
                .body("login", equalTo("test@example.com"))
                .body("firstname", equalTo("Иван"))
                .body("lastname", equalTo("Иванов"));
    }

    @Test
    void testGetAllEditors() {
        given()
                .port(port)
                .when()
                .get("/api/v1.0/editors")
                .then()
                .statusCode(200)
                .body("content", notNullValue());
    }

    @Test
    void testUpdateEditor() {
        // Сначала создаем редактора
        String createJson = """
            {
                "login": "update@example.com",
                "password": "password123",
                "firstname": "Старое",
                "lastname": "Имя"
            }
            """;

        Long editorId = given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(createJson)
                .when()
                .post("/api/v1.0/editors")
                .then()
                .statusCode(201)
                .extract()
                .jsonPath()
                .getLong("id");

        // Обновляем редактора
        String updateJson = """
            {
                "login": "updated@example.com",
                "password": "newpassword123",
                "firstname": "Новое",
                "lastname": "Имя"
            }
            """;

        given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(updateJson)
                .when()
                .put("/api/v1.0/editors/{id}", editorId)
                .then()
                .statusCode(200)
                .body("firstname", equalTo("Новое"))
                .body("lastname", equalTo("Имя"));
    }

    @Test
    void testDeleteEditor() {
        // Создаем редактора для удаления
        String createJson = """
            {
                "login": "delete@example.com",
                "password": "password123",
                "firstname": "Удаляемый",
                "lastname": "Редактор"
            }
            """;

        Long editorId = given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(createJson)
                .when()
                .post("/api/v1.0/editors")
                .then()
                .statusCode(201)
                .extract()
                .jsonPath()
                .getLong("id");

        // Удаляем редактора
        given()
                .port(port)
                .when()
                .delete("/api/v1.0/editors/{id}", editorId)
                .then()
                .statusCode(204);

        // Проверяем, что редактор удален
        given()
                .port(port)
                .when()
                .get("/api/v1.0/editors/{id}", editorId)
                .then()
                .statusCode(404);
    }
}