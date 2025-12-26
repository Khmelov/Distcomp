package com.restApp.controller;

import com.restApp.AbstractIntegrationTest;
import com.restApp.dto.AuthorRequestTo;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

class AuthorControllerTest extends AbstractIntegrationTest {

    @Test
    void testCRUD() {
        // Create
        AuthorRequestTo request = new AuthorRequestTo();
        request.setLogin("testUser");
        request.setPassword("password123");
        request.setFirstname("John");
        request.setLastname("Doe");

        Integer id = given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/authors")
                .then()
                .statusCode(201)
                .body("login", equalTo("testUser"))
                .extract().path("id");

        // Read
        given()
                .when()
                .get("/authors/" + id)
                .then()
                .statusCode(200)
                .body("firstname", equalTo("John"));

        // Update
        request.setFirstname("Jane");
        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .put("/authors/" + id)
                .then()
                .statusCode(200)
                .body("firstname", equalTo("Jane"));

        // Delete
        given()
                .when()
                .delete("/authors/" + id)
                .then()
                .statusCode(204);

        // Verify Delete
        given()
                .when()
                .get("/authors/" + id)
                .then()
                .statusCode(404); // Should be 404
    }

    @Test
    void testValidation() {
        AuthorRequestTo request = new AuthorRequestTo();
        request.setLogin(""); // Invalid
        request.setPassword("123"); // Invalid size

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/authors")
                .then()
                .statusCode(400); // Bad Request
    }
}
