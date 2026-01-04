package com.task.rest.controller;

import com.task.rest.BaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

class MarkControllerTest extends BaseIntegrationTest {

    @Test
    void testCreateMark_Success() {
        String requestBody = """
            {
                "name": "testmark"
            }
            """;

        given()
                .spec(requestSpec)
                .body(requestBody)
                .when()
                .post("/api/v1.0/marks")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .body("name", equalTo("testmark"))
                .body("id", notNullValue());
    }

    @Test
    void testCreateMark_InvalidName() {
        String requestBody = """
            {
                "name": ""
            }
            """;

        given()
                .spec(requestSpec)
                .body(requestBody)
                .when()
                .post("/api/v1.0/marks")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void testGetMarkById_Success() {
        String requestBody = """
            {
                "name": "getbyidmark"
            }
            """;

        Integer markId = given()
                .spec(requestSpec)
                .body(requestBody)
                .when()
                .post("/api/v1.0/marks")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .path("id");

        given()
                .spec(requestSpec)
                .when()
                .get("/api/v1.0/marks/{id}", markId)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("id", equalTo(markId))
                .body("name", equalTo("getbyidmark"));
    }

    @Test
    void testGetMarkById_NotFound() {
        given()
                .spec(requestSpec)
                .when()
                .get("/api/v1.0/marks/{id}", 99999)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void testGetAllMarks_Success() {
        for (int i = 1; i <= 3; i++) {
            String requestBody = String.format("""
                {
                    "name": "mark%d"
                }
                """, i);

            given()
                    .spec(requestSpec)
                    .body(requestBody)
                    .when()
                    .post("/api/v1.0/marks");
        }

        given()
                .spec(requestSpec)
                .when()
                .get("/api/v1.0/marks")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("size()", greaterThanOrEqualTo(3));
    }

    @Test
    void testUpdateMark_Success() {
        String createBody = """
            {
                "name": "oldmarkname"
            }
            """;

        Integer markId = given()
                .spec(requestSpec)
                .body(createBody)
                .when()
                .post("/api/v1.0/marks")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .path("id");

        String updateBody = """
            {
                "name": "newmarkname"
            }
            """;

        given()
                .spec(requestSpec)
                .body(updateBody)
                .when()
                .put("/api/v1.0/marks/{id}", markId)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("name", equalTo("newmarkname"));
    }

    @Test
    void testDeleteMark_Success() {
        String requestBody = """
            {
                "name": "deletemark"
            }
            """;

        Integer markId = given()
                .spec(requestSpec)
                .body(requestBody)
                .when()
                .post("/api/v1.0/marks")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .path("id");

        given()
                .spec(requestSpec)
                .when()
                .delete("/api/v1.0/marks/{id}", markId)
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());

        given()
                .spec(requestSpec)
                .when()
                .get("/api/v1.0/marks/{id}", markId)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }
}
