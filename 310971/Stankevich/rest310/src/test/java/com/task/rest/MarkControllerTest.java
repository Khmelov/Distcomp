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
public class MarkControllerTest {

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";
        RestAssured.basePath = "/api/v1.0";
    }

    @Test
    void createMark_shouldReturnCreatedStatus() {
        String markJson = """
                {
                    "name": "Technology"
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(markJson)
                .when()
                .post("/marks")
                .then()
                .statusCode(201)
                .body("name", equalTo("Technology"))
                .body("id", notNullValue());
    }

    @Test
    void getAllMarks_shouldReturnOk() {
        given()
                .when()
                .get("/marks")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON);
    }

    @Test
    void getMarkById_shouldReturnOk_whenExists() {
        // Создаём метку
        String markJson = """
                {
                    "name": "FindMe"
                }
                """;

        Integer id = given()
                .contentType(ContentType.JSON)
                .body(markJson)
                .when()
                .post("/marks")
                .then()
                .statusCode(201)
                .extract()
                .path("id");

        // Получаем по ID
        given()
                .when()
                .get("/marks/" + id)
                .then()
                .statusCode(200)
                .body("id", equalTo(id))
                .body("name", equalTo("FindMe"));
    }

    @Test
    void updateMark_shouldReturnOk() {
        // Создаём метку
        String createJson = """
                {
                    "name": "OldName"
                }
                """;

        Integer id = given()
                .contentType(ContentType.JSON)
                .body(createJson)
                .when()
                .post("/marks")
                .then()
                .statusCode(201)
                .extract()
                .path("id");

        // Обновляем
        String updateJson = String.format("""
                {
                    "id": %d,
                    "name": "NewName"
                }
                """, id);

        given()
                .contentType(ContentType.JSON)
                .body(updateJson)
                .when()
                .put("/marks")
                .then()
                .statusCode(200)
                .body("name", equalTo("NewName"));
    }

    @Test
    void deleteMark_shouldReturnNoContent() {
        // Создаём метку
        String markJson = """
                {
                    "name": "DeleteMe"
                }
                """;

        Integer id = given()
                .contentType(ContentType.JSON)
                .body(markJson)
                .when()
                .post("/marks")
                .then()
                .statusCode(201)
                .extract()
                .path("id");

        // Удаляем
        given()
                .when()
                .delete("/marks/" + id)
                .then()
                .statusCode(204);

        // Проверяем что удалена
        given()
                .when()
                .get("/marks/" + id)
                .then()
                .statusCode(404);
    }

    @Test
    void createMark_withShortName_shouldReturnBadRequest() {
        String invalidJson = """
                {
                    "name": "a"
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(invalidJson)
                .when()
                .post("/marks")
                .then()
                .statusCode(400);
    }

    @Test
    void createMark_withLongName_shouldReturnBadRequest() {
        String invalidJson = String.format("""
                {
                    "name": "%s"
                }
                """, "a".repeat(33));

        given()
                .contentType(ContentType.JSON)
                .body(invalidJson)
                .when()
                .post("/marks")
                .then()
                .statusCode(400);
    }

    @Test
    void createMark_withBlankName_shouldReturnBadRequest() {
        String invalidJson = """
                {
                    "name": ""
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(invalidJson)
                .when()
                .post("/marks")
                .then()
                .statusCode(400);
    }

    @Test
    void createMark_withNullName_shouldReturnBadRequest() {
        String invalidJson = """
                {
                    "name": null
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(invalidJson)
                .when()
                .post("/marks")
                .then()
                .statusCode(400);
    }

    @Test
    void getMark_withNonExistentId_shouldReturnNotFound() {
        given()
                .when()
                .get("/marks/99999")
                .then()
                .statusCode(404);
    }
}