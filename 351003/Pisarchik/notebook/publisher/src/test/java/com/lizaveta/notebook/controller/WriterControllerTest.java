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
class WriterControllerTest extends PostgresTestContainerConfig {

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";
    }

    @Test
    void createWriter_ShouldReturn201AndWriter() {
        String body = """
                {"writer":{"login":"ririgly.4@gmail.com","password":"password123","firstname":"Елизавета","lastname":"Писарчик"}}
                """;
        given()
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post("/api/v1.0/writers")
                .then()
                .statusCode(201)
                .body("writer.id", notNullValue())
                .body("writer.login", equalTo("ririgly.4@gmail.com"))
                .body("writer.firstname", equalTo("Елизавета"))
                .body("writer.lastname", equalTo("Писарчик"));
    }

    @Test
    void getWriters_ShouldReturn200AndPage() {
        given()
                .when()
                .get("/api/v1.0/writers")
                .then()
                .statusCode(200)
                .body("content", notNullValue())
                .body("totalElements", notNullValue())
                .body("totalPages", notNullValue());
    }

    @Test
    void getWriterById_WhenExists_ShouldReturn200() {
        String body = """
                {"writer":{"login":"test@test.com","password":"password12","firstname":"Test","lastname":"User"}}
                """;
        Long id = given()
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post("/api/v1.0/writers")
                .then()
                .statusCode(201)
                .extract()
                .path("writer.id");
        given()
                .when()
                .get("/api/v1.0/writers/" + id)
                .then()
                .statusCode(200)
                .body("writer.id", equalTo(id.intValue()))
                .body("writer.login", equalTo("test@test.com"));
    }

    @Test
    void getWriterById_WhenNotExists_ShouldReturn404() {
        given()
                .when()
                .get("/api/v1.0/writers/99999")
                .then()
                .statusCode(404)
                .body("errorMessage", notNullValue())
                .body("errorCode", equalTo(40401));
    }

    @Test
    void updateWriter_ShouldReturn200() {
        String createBody = """
                {"writer":{"login":"old@test.com","password":"password12","firstname":"Old","lastname":"Name"}}
                """;
        Long id = given()
                .contentType(ContentType.JSON)
                .body(createBody)
                .when()
                .post("/api/v1.0/writers")
                .then()
                .statusCode(201)
                .extract()
                .path("writer.id");
        String updateBody = """
                {"writer":{"login":"new@test.com","password":"newpass123","firstname":"New","lastname":"Name"}}
                """;
        given()
                .contentType(ContentType.JSON)
                .body(updateBody)
                .when()
                .put("/api/v1.0/writers/" + id)
                .then()
                .statusCode(200)
                .body("writer.login", equalTo("new@test.com"))
                .body("writer.firstname", equalTo("New"));
    }

    @Test
    void deleteWriter_ShouldReturn204() {
        String body = """
                {"writer":{"login":"del@test.com","password":"password12","firstname":"Del","lastname":"User"}}
                """;
        Long id = given()
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post("/api/v1.0/writers")
                .then()
                .statusCode(201)
                .extract()
                .path("writer.id");
        given()
                .when()
                .delete("/api/v1.0/writers/" + id)
                .then()
                .statusCode(204);
        given()
                .when()
                .get("/api/v1.0/writers/" + id)
                .then()
                .statusCode(404);
    }

    @Test
    void createWriter_WhenInvalid_ShouldReturn400() {
        String body = """
                {"writer":{"login":"a","password":"short","firstname":"x","lastname":"y"}}
                """;
        given()
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post("/api/v1.0/writers")
                .then()
                .statusCode(400)
                .body("errorMessage", notNullValue())
                .body("errorCode", equalTo(40001));
    }

    @Test
    void getWriterById_WhenInvalidId_ShouldReturn400() {
        given()
                .when()
                .get("/api/v1.0/writers/0")
                .then()
                .statusCode(400)
                .body("errorMessage", notNullValue())
                .body("errorCode", equalTo(40004));
    }
}
