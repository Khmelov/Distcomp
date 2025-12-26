package com.restApp.controller;

import com.restApp.dto.MarkRequestTo;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MarkControllerTest {

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @Test
    void testCreateAndGetMark() {
        MarkRequestTo request = new MarkRequestTo();
        request.setName("Important");

        Integer id = given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/api/v1.0/marks")
                .then()
                .statusCode(201)
                .body("name", equalTo("Important"))
                .extract().path("id");

        given()
                .when()
                .get("/api/v1.0/marks/" + id)
                .then()
                .statusCode(200)
                .body("name", equalTo("Important"));
    }
}
