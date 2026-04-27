package com.example.lab.test;

import static org.hamcrest.Matchers.notNullValue;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import static io.restassured.RestAssured.given;
import io.restassured.http.ContentType;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SecurityV2IntegrationTest {

    @LocalServerPort
    int port;

    @Test
    void v1_stays_public() {
        given()
                .port(port)
                .when()
                .get("/api/v1.0/users")
                .then()
                .statusCode(200);
    }

    @Test
    void v2_requires_auth() {
        given()
                .port(port)
                .when()
                .get("/api/v2.0/news")
                .then()
                .statusCode(401);
    }

    @Test
    void registration_and_login_issue_jwt() {
        String login = "sec_user_" + System.currentTimeMillis();

        given()
                .port(port)
                .contentType(ContentType.JSON)
                .body("""
                        {
                          "login": "%s",
                          "password": "password123",
                          "firstname": "John",
                          "lastname": "Doe",
                          "role": "CUSTOMER"
                        }
                        """.formatted(login))
                .when()
                .post("/api/v2.0/users")
                .then()
                .statusCode(201);

        String token = given()
                .port(port)
                .contentType(ContentType.JSON)
                .body("""
                        {
                          "login": "%s",
                          "password": "password123"
                        }
                        """.formatted(login))
                .when()
                .post("/api/v2.0/login")
                .then()
                .statusCode(200)
                .body("access_token", notNullValue())
                .extract()
                .path("access_token");

        given()
                .port(port)
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/api/v2.0/users/me")
                .then()
                .statusCode(200)
                .body("login", notNullValue())
                .body("role", notNullValue());
    }
}

