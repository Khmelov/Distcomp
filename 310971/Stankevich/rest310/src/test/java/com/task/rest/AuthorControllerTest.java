package com.task.rest;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class AuthorControllerTest {

    @BeforeAll
    public static void setup() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 24110;
        RestAssured.basePath = "/api/v1.0";
    }

    @Test
    void createAuthor_shouldReturnCreatedStatus() {
        String authorJson = """
                {
                    "login": "johndoe",
                    "password": "password123",
                    "firstname": "John",
                    "lastname": "Doe"
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(authorJson)
                .when()
                .post("/authors")
                .then()
                .statusCode(201)
                .body("login", equalTo("johndoe"))
                .body("firstname", equalTo("John"))
                .body("lastname", equalTo("Doe"))
                .body("id", notNullValue());
    }

    @Test
    void getAllAuthors_shouldReturnOk() {
        given()
                .when()
                .get("/authors")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON);
    }

    @Test
    void getAuthorById_shouldReturnOk_whenExists() {
        // Создаём автора
        String authorJson = """
                {
                    "login": "testuser",
                    "password": "password123",
                    "firstname": "Test",
                    "lastname": "User"
                }
                """;

        Integer id = given()
                .contentType(ContentType.JSON)
                .body(authorJson)
                .when()
                .post("/authors")
                .then()
                .statusCode(201)
                .extract()
                .path("id");

        // Получаем по ID
        given()
                .when()
                .get("/authors/" + id)
                .then()
                .statusCode(200)
                .body("id", equalTo(id))
                .body("login", equalTo("testuser"));
    }

    @Test
    void updateAuthor_shouldReturnOk() {
        // Создаём автора
        String createJson = """
                {
                    "login": "updateuser",
                    "password": "password123",
                    "firstname": "Old",
                    "lastname": "Name"
                }
                """;

        Integer id = given()
                .contentType(ContentType.JSON)
                .body(createJson)
                .when()
                .post("/authors")
                .then()
                .statusCode(201)
                .extract()
                .path("id");

        // Обновляем
        String updateJson = String.format("""
                {
                    "id": %d,
                    "login": "updateuser",
                    "password": "password123",
                    "firstname": "Updated",
                    "lastname": "Name"
                }
                """, id);

        given()
                .contentType(ContentType.JSON)
                .body(updateJson)
                .when()
                .put("/authors")
                .then()
                .statusCode(200)
                .body("firstname", equalTo("Updated"));
    }

    @Test
    void deleteAuthor_shouldReturnNoContent() {
        // Создаём автора
        String authorJson = """
                {
                    "login": "deleteuser",
                    "password": "password123",
                    "firstname": "Delete",
                    "lastname": "Me"
                }
                """;

        Integer id = given()
                .contentType(ContentType.JSON)
                .body(authorJson)
                .when()
                .post("/authors")
                .then()
                .statusCode(201)
                .extract()
                .path("id");

        // Удаляем
        given()
                .when()
                .delete("/authors/" + id)
                .then()
                .statusCode(204);

        // Проверяем что удалён
        given()
                .when()
                .get("/authors/" + id)
                .then()
                .statusCode(404);
    }

    @Test
    void createAuthor_withShortLogin_shouldReturnBadRequest() {
        String invalidJson = """
                {
                    "login": "a",
                    "password": "password123",
                    "firstname": "John",
                    "lastname": "Doe"
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(invalidJson)
                .when()
                .post("/authors")
                .then()
                .statusCode(400);
    }

    @Test
    void createAuthor_withShortPassword_shouldReturnBadRequest() {
        String invalidJson = """
                {
                    "login": "validlogin",
                    "password": "short12",
                    "firstname": "John",
                    "lastname": "Doe"
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(invalidJson)
                .when()
                .post("/authors")
                .then()
                .statusCode(400);
    }

    @Test
    void createAuthor_withShortFirstname_shouldReturnBadRequest() {
        String invalidJson = """
                {
                    "login": "validlogin",
                    "password": "password123",
                    "firstname": "J",
                    "lastname": "Doe"
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(invalidJson)
                .when()
                .post("/authors")
                .then()
                .statusCode(400);
    }

    @Test
    void getAuthor_withNonExistentId_shouldReturnNotFound() {
        given()
                .when()
                .get("/authors/99999")
                .then()
                .statusCode(404);
    }
}