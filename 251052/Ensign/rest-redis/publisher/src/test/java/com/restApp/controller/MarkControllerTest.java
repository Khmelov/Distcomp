package com.restApp.controller;

import com.restApp.AbstractIntegrationTest;
import com.restApp.dto.MarkRequestTo;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

class MarkControllerTest extends AbstractIntegrationTest {

    @Test
    void testCRUD() {
        MarkRequestTo request = new MarkRequestTo();
        request.setName("Important");

        // Create
        Integer id = given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/marks")
                .then()
                .statusCode(201)
                .body("name", equalTo("Important"))
                .extract().path("id");

        // Read
        given()
                .when()
                .get("/marks/" + id)
                .then()
                .statusCode(200)
                .body("name", equalTo("Important"));

        // Update
        request.setName("Very Important");
        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .put("/marks/" + id)
                .then()
                .statusCode(200)
                .body("name", equalTo("Very Important"));

        // Delete
        given()
                .when()
                .delete("/marks/" + id)
                .then()
                .statusCode(204);
    }
}
