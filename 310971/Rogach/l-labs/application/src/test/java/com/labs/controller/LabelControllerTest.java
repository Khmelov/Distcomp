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
public class LabelControllerTest {

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";
    }

    @Test
    void testCreateLabel() {
        String requestBody = """
                {
                    "name": "TestLabel"
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/v1/labels")
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("name", equalTo("TestLabel"));
    }

    @Test
    void testGetAllLabels() {
        given()
                .when()
                .get("/api/v1/labels")
                .then()
                .statusCode(200)
                .body("$", instanceOf(java.util.List.class));
    }

    @Test
    void testGetLabelById() {
        String createBody = """
                {
                    "name": "GetTestLabel"
                }
                """;

        Long id = given()
                .contentType(ContentType.JSON)
                .body(createBody)
                .when()
                .post("/api/v1/labels")
                .then()
                .extract()
                .path("id");

        given()
                .when()
                .get("/api/v1/labels/" + id)
                .then()
                .statusCode(200)
                .body("id", equalTo(id.intValue()))
                .body("name", equalTo("GetTestLabel"));
    }

    @Test
    void testUpdateLabel() {
        String createBody = """
                {
                    "name": "OriginalLabel"
                }
                """;

        Long id = given()
                .contentType(ContentType.JSON)
                .body(createBody)
                .when()
                .post("/api/v1/labels")
                .then()
                .extract()
                .path("id");

        String updateBody = """
                {
                    "name": "UpdatedLabel"
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(updateBody)
                .when()
                .put("/api/v1/labels/" + id)
                .then()
                .statusCode(200)
                .body("id", equalTo(id.intValue()))
                .body("name", equalTo("UpdatedLabel"));
    }

    @Test
    void testDeleteLabel() {
        String createBody = """
                {
                    "name": "DeleteLabel"
                }
                """;

        Long id = given()
                .contentType(ContentType.JSON)
                .body(createBody)
                .when()
                .post("/api/v1/labels")
                .then()
                .extract()
                .path("id");

        given()
                .when()
                .delete("/api/v1/labels/" + id)
                .then()
                .statusCode(204);

        given()
                .when()
                .get("/api/v1/labels/" + id)
                .then()
                .statusCode(404);
    }

    @Test
    void testCreateLabelWithInvalidData() {
        String requestBody = """
                {
                    "name": "a"
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/v1/labels")
                .then()
                .statusCode(400);
    }

    @Test
    void testGetLabelByIdNotFound() {
        given()
                .when()
                .get("/api/v1/labels/99999")
                .then()
                .statusCode(404)
                .body("errorCode", equalTo("40401"));
    }
}

