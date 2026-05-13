package org.example;

import io.restassured.http.ContentType;
import org.example.dto.EditorRequestTo;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class EditorControllerTest extends BaseTest {

    @Test
    void shouldCreateAndFindEditor() {
        EditorRequestTo request = new EditorRequestTo(null, "dev_user", "pass123456", "Ivan", "Ivanov");

        // Создание
        Long id = given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/editors")
                .then()
                .statusCode(201)
                .body("login", equalTo("dev_user"))
                .extract().path("id");

        // Получение по ID
        given()
                .when()
                .get("/editors/" + id)
                .then()
                .statusCode(200)
                .body("firstname", equalTo("Ivan"));
    }

    @Test
    void shouldReturn404WhenEditorNotFound() {
        given()
                .when()
                .get("/editors/9999")
                .then()
                .statusCode(404)
                .body("errorCode", equalTo("40401"));
    }
}