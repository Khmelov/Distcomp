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
public class WriterControllerTest {

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";
    }

    @Test
    void testCreateWriter() {
        String requestBody = """
                {
                    "login": "test@example.com",
                    "password": "password123",
                    "firstname": "John",
                    "lastname": "Doe"
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/v1/writers")
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("login", equalTo("test@example.com"))
                .body("firstname", equalTo("John"))
                .body("lastname", equalTo("Doe"));
    }

    @Test
    void testGetAllWriters() {
        given()
                .when()
                .get("/api/v1/writers")
                .then()
                .statusCode(200)
                .body("$", instanceOf(java.util.List.class));
    }

    @Test
    void testGetWriterById() {
        // First create a writer
        String requestBody = """
                {
                    "login": "gettest@example.com",
                    "password": "password123",
                    "firstname": "Jane",
                    "lastname": "Smith"
                }
                """;

        Long id = given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/v1/writers")
                .then()
                .extract()
                .path("id");

        // Then get it by id
        given()
                .when()
                .get("/api/v1/writers/" + id)
                .then()
                .statusCode(200)
                .body("id", equalTo(id.intValue()))
                .body("login", equalTo("gettest@example.com"));
    }

    @Test
    void testUpdateWriter() {
        // First create a writer
        String createBody = """
                {
                    "login": "updatetest@example.com",
                    "password": "password123",
                    "firstname": "Original",
                    "lastname": "Name"
                }
                """;

        Long id = given()
                .contentType(ContentType.JSON)
                .body(createBody)
                .when()
                .post("/api/v1/writers")
                .then()
                .extract()
                .path("id");

        // Then update it
        String updateBody = """
                {
                    "login": "updated@example.com",
                    "password": "newpassword123",
                    "firstname": "Updated",
                    "lastname": "Name"
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(updateBody)
                .when()
                .put("/api/v1/writers/" + id)
                .then()
                .statusCode(200)
                .body("id", equalTo(id.intValue()))
                .body("login", equalTo("updated@example.com"))
                .body("firstname", equalTo("Updated"));
    }

    @Test
    void testDeleteWriter() {
        // First create a writer
        String requestBody = """
                {
                    "login": "deletetest@example.com",
                    "password": "password123",
                    "firstname": "Delete",
                    "lastname": "Test"
                }
                """;

        Long id = given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/v1/writers")
                .then()
                .extract()
                .path("id");

        // Then delete it
        given()
                .when()
                .delete("/api/v1/writers/" + id)
                .then()
                .statusCode(204);

        // Verify it's deleted
        given()
                .when()
                .get("/api/v1/writers/" + id)
                .then()
                .statusCode(404);
    }

    @Test
    void testCreateWriterWithInvalidData() {
        String requestBody = """
                {
                    "login": "a",
                    "password": "short",
                    "firstname": "",
                    "lastname": ""
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/v1/writers")
                .then()
                .statusCode(400);
    }

    @Test
    void testGetWriterByIdNotFound() {
        given()
                .when()
                .get("/api/v1/writers/99999")
                .then()
                .statusCode(404)
                .body("errorCode", equalTo("40401"));
    }
}

