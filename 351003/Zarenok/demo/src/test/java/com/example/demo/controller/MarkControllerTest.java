package com.example.demo.controller;

import com.example.demo.controller.BaseIntegrationTest;
import com.example.demo.dto.requests.MarkRequestTo;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class MarkControllerTest extends BaseIntegrationTest {

    private final String BASE_URL = "/marks";

    private String uniqueName() {
        return "mark_" + UUID.randomUUID().toString().substring(0, 8);
    }

    @Test
    void createMark_ValidData_ShouldReturn201() {
        MarkRequestTo request = new MarkRequestTo();
        request.setName(uniqueName());

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post(BASE_URL)
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("name", equalTo(request.getName()));
    }

    @Test
    void createMark_InvalidData_ShouldReturn400() {
        MarkRequestTo request = new MarkRequestTo();
        request.setName("a"); // слишком короткий

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post(BASE_URL)
                .then()
                .statusCode(400);
    }

    @Test
    void getMarkById_ExistingId_ShouldReturn200() {
        Long id = createTestMark(uniqueName());

        given()
                .when()
                .get(BASE_URL + "/{id}", id)
                .then()
                .statusCode(200)
                .body("id", equalTo(id.intValue()))
                .body("name", notNullValue());
    }

    @Test
    void getMarkById_NotFound_ShouldReturn404() {
        given()
                .when()
                .get(BASE_URL + "/{id}", 9999L)
                .then()
                .statusCode(404);
    }

    @Test
    void getAllMarks_ShouldReturn200() {
        createTestMark(uniqueName());
        createTestMark(uniqueName());

        given()
                .when()
                .get(BASE_URL)
                .then()
                .statusCode(200)
                .body("size()", greaterThanOrEqualTo(2));
    }

    @Test
    void updateMark_ValidData_ShouldReturn200() {
        Long id = createTestMark(uniqueName());

        MarkRequestTo updateReq = new MarkRequestTo();
        updateReq.setName(uniqueName());

        given()
                .contentType(ContentType.JSON)
                .body(updateReq)
                .when()
                .put(BASE_URL + "/{id}", id)
                .then()
                .statusCode(200)
                .body("id", equalTo(id.intValue()))
                .body("name", equalTo(updateReq.getName()));
    }

    @Test
    void deleteMark_ShouldReturn204() {
        Long id = createTestMark(uniqueName());

        given()
                .when()
                .delete(BASE_URL + "/{id}", id)
                .then()
                .statusCode(204);

        given()
                .when()
                .get(BASE_URL + "/{id}", id)
                .then()
                .statusCode(404);
    }

    // Хелпер
    private Long createTestMark(String name) {
        MarkRequestTo req = new MarkRequestTo();
        req.setName(name);
        return given()
                .contentType(ContentType.JSON)
                .body(req)
                .post(BASE_URL)
                .then()
                .statusCode(201)
                .extract()
                .jsonPath()
                .getLong("id");
    }
}