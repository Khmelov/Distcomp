package org.example;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EditorControllerTest {

    @LocalServerPort
    private int port;

    @BeforeEach
    public void setup() {
        RestAssured.port = port;
        RestAssured.basePath = "/api/v1.0";
    }

    @Test
    public void testCreateAndGetEditor() {
        String editorJson = """
                {
                  "editor": {
                    "login": "test_user",
                    "password": "password123",
                    "firstname": "Ivan",
                    "lastname": "Ivanov"
                  }
                }
                """;

        // Create
        Long id = given()
                .contentType(ContentType.JSON)
                .body(editorJson)
                .when()
                .post("/editors")
                .then()
                .statusCode(201)
                .body("editor.login", equalTo("test_user"))
                .extract().path("editor.id");

        // Get by ID
        given()
                .when()
                .get("/editors/" + id)
                .then()
                .statusCode(200)
                .body("editor.firstname", equalTo("Ivan"));
    }

    @Test
    public void testDeleteNotFound() {
        given()
                .when()
                .delete("/editors/999")
                .then()
                .statusCode(404)
                .body("errorCode", equalTo("40401"));
    }
}