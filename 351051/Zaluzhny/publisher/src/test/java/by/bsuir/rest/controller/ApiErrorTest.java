package by.bsuir.rest.controller;

import by.bsuir.rest.support.ApiTestSupport;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;

class ApiErrorTest extends ApiTestSupport {

    @Test
    @DisplayName("Битый JSON, неверный тип id, чужой метод и Content-Type дают коды 4xx")
    void reportsRequestErrors() {
        assertError(given()
                .contentType(ContentType.JSON)
                .body("{")
                .when()
                .post("/users")
                .then(), 400, 40006);

        assertError(given()
                .contentType(ContentType.JSON)
                .body("{}")
                .when()
                .post("/markers")
                .then(), 400, 40001);

        assertError(when().get("/users/{id}", "abc").then(), 400, 40005);
        assertError(when().delete("/users").then(), 405, 40501);
        assertError(given()
                .contentType(ContentType.TEXT)
                .body("text")
                .when()
                .post("/users")
                .then(), 415, 41501);
    }
}
