package com.example.distcomp.controller

import com.example.distcomp.dto.request.CreatorRequestTo
import io.restassured.RestAssured
import io.restassured.http.ContentType
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

class CreatorControllerTest : BaseControllerTest() {

    @LocalServerPort
    private var port: Int = 0

    @BeforeEach
    fun setUp() {
        RestAssured.port = port
    }

    @Test
    fun `GIVEN a valid creator request WHEN posting to api THEN it should return 201 Created`() {
        val request = CreatorRequestTo(
            login = "api_test@example.com",
            password = "password",
            firstname = "API",
            lastname = "Test"
        )

        RestAssured.given()
            .contentType(ContentType.JSON)
            .body(request)
            .`when`()
            .post("/api/v1.0/creators")
            .then()
            .statusCode(201)
            .body("login", equalTo("api_test@example.com"))
            .body("id", notNullValue())
    }

    @Test
    fun `GIVEN an invalid creator request WHEN posting to api THEN it should return 400 with 5-digit error code`() {
        val request = CreatorRequestTo(
            login = "a", // too short (Size min=2)
            password = "p",
            firstname = "f",
            lastname = "l"
        )

        RestAssured.given()
            .contentType(ContentType.JSON)
            .body(request)
            .`when`()
            .post("/api/v1.0/creators")
            .then()
            .log().ifValidationFails()
            .statusCode(400)
            .body("errorCode", equalTo(40002))
            .body("errorMessage", notNullValue())
    }

    @Test
    fun `GIVEN existing creators WHEN getting all with pagination THEN it should return paged results`() {
        // Ensure some data exists
        listOf("u1@ex.com", "u2@ex.com", "u3@ex.com").forEach { login ->
            RestAssured.given()
                .contentType(ContentType.JSON)
                .body(CreatorRequestTo(login, "password123", "First", "Last"))
                .post("/api/v1.0/creators")
                .then()
                .statusCode(201)
        }

        RestAssured.given()
            .queryParam("page", 0)
            .queryParam("size", 2)
            .queryParam("sort", "id,asc")
            .`when`()
            .get("/api/v1.0/creators")
            .then()
            .statusCode(200)
            .body("$.size()", equalTo(2))
    }
}
