package com.example.lab.test;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.*;
import org.springframework.http.HttpStatus;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MarkerControllerIntegrationTest extends BaseIntegrationTest {

    private static Long markerId;

    @Test
    @Order(1)
    @DisplayName("STEP 1: CREATE MARKER - Should return 201 Created")
    void createMarker() {
        String payload = """
                {
                    "name": "Test Marker"
                }
                """;

        markerId = given()
                .baseUri(getBaseUrl())
                .contentType(ContentType.JSON)
                .body(payload)
                .when()
                .post("/api/v1.0/markers")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .contentType(ContentType.JSON)
                .body("id", notNullValue())
                .body("name", equalTo("Test Marker"))
                .extract()
                .jsonPath()
                .getLong("id");

        Assertions.assertNotNull(markerId);
    }

    @Test
    @Order(2)
    @DisplayName("STEP 2: GET MARKER BY ID - Should return 200 OK")
    void getMarkerById() {
        given()
                .baseUri(getBaseUrl())
                .when()
                .get("/api/v1.0/markers/{id}", markerId)
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body("id", equalTo(markerId.intValue()))
                .body("name", equalTo("Test Marker"));
    }

    @Test
    @Order(3)
    @DisplayName("STEP 3: UPDATE MARKER - Should return 200 OK")
    void updateMarker() {
        String payload = """
                {
                    "id": %d,
                    "name": "Updated Marker"
                }
                """.formatted(markerId);

        given()
                .baseUri(getBaseUrl())
                .contentType(ContentType.JSON)
                .body(payload)
                .when()
                .put("/api/v1.0/markers/{id}", markerId)
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body("id", equalTo(markerId.intValue()))
                .body("name", equalTo("Updated Marker"));
    }

    @Test
    @Order(4)
    @DisplayName("STEP 4: DELETE MARKER - Should return 204 No Content")
    void deleteMarker() {
        given()
                .baseUri(getBaseUrl())
                .when()
                .delete("/api/v1.0/markers/{id}", markerId)
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }

    @Test
    @Order(5)
    @DisplayName("STEP 5: GET DELETED MARKER - Should return 404 Not Found")
    void getDeletedMarker() {
        given()
                .baseUri(getBaseUrl())
                .when()
                .get("/api/v1.0/markers/{id}", markerId)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .contentType(ContentType.JSON)
                .body("errorCode", equalTo(40401));
    }

    @Test
    @Order(6)
    @DisplayName("NEGATIVE: CREATE MARKER WITH INVALID DATA - Should return 400 Bad Request")
    void createMarkerWithInvalidData() {
        String payload = """
                {
                    "name": ""
                }
                """;

        given()
                .baseUri(getBaseUrl())
                .contentType(ContentType.JSON)
                .body(payload)
                .when()
                .post("/api/v1.0/markers")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @Order(7)
    @DisplayName("NEGATIVE: UPDATE NON-EXISTENT MARKER - Should return 404 Not Found")
    void updateNonExistentMarker() {
        String payload = """
                {
                    "id": 999999,
                    "name": "Non-existent"
                }
                """;

        given()
                .baseUri(getBaseUrl())
                .contentType(ContentType.JSON)
                .body(payload)
                .when()
                .put("/api/v1.0/markers/{id}", 999999)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body("errorCode", equalTo(40401));
    }
}