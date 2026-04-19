package com.lizaveta.notebook.controller;

import com.lizaveta.notebook.config.PostgresTestContainerConfig;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MarkerControllerTest extends PostgresTestContainerConfig {

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";
    }

    @Test
    void createMarker_ShouldReturn201() {
        given()
                .contentType(ContentType.JSON)
                .body("{\"marker\":{\"name\":\"fantasy\"}}")
                .when()
                .post("/api/v1.0/markers")
                .then()
                .statusCode(201)
                .body("marker.id", notNullValue())
                .body("marker.name", equalTo("fantasy"));
    }

    @Test
    void getMarkers_ShouldReturn200AndPage() {
        given()
                .when()
                .get("/api/v1.0/markers")
                .then()
                .statusCode(200)
                .body("content", notNullValue())
                .body("totalElements", notNullValue())
                .body("totalPages", notNullValue());
    }

    @Test
    void getMarkerById_WhenExists_ShouldReturn200() {
        Long id = given()
                .contentType(ContentType.JSON)
                .body("{\"marker\":{\"name\":\"adventure\"}}")
                .when()
                .post("/api/v1.0/markers")
                .then()
                .statusCode(201)
                .extract()
                .path("marker.id");
        given()
                .when()
                .get("/api/v1.0/markers/" + id)
                .then()
                .statusCode(200)
                .body("marker.id", equalTo(id.intValue()))
                .body("marker.name", equalTo("adventure"));
    }

    @Test
    void updateMarker_ShouldReturn200() {
        Long id = given()
                .contentType(ContentType.JSON)
                .body("{\"marker\":{\"name\":\"old-name\"}}")
                .when()
                .post("/api/v1.0/markers")
                .then()
                .statusCode(201)
                .extract()
                .path("marker.id");
        given()
                .contentType(ContentType.JSON)
                .body("{\"marker\":{\"name\":\"new-name\"}}")
                .when()
                .put("/api/v1.0/markers/" + id)
                .then()
                .statusCode(200)
                .body("marker.name", equalTo("new-name"));
    }

    @Test
    void deleteMarker_ShouldReturn204() {
        Long id = given()
                .contentType(ContentType.JSON)
                .body("{\"marker\":{\"name\":\"to-delete\"}}")
                .when()
                .post("/api/v1.0/markers")
                .then()
                .statusCode(201)
                .extract()
                .path("marker.id");
        given()
                .when()
                .delete("/api/v1.0/markers/" + id)
                .then()
                .statusCode(204);
        given()
                .when()
                .get("/api/v1.0/markers/" + id)
                .then()
                .statusCode(404);
    }
}
