package com.example.distcomp.controller

import io.restassured.RestAssured
import io.restassured.http.ContentType
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.notNullValue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.web.server.LocalServerPort

class SecurityControllerTest : BaseControllerTest() {

    @LocalServerPort
    private var port: Int = 0

    @BeforeEach
    fun setUp() {
        RestAssured.port = port
    }

    @Test
    fun `registration login and me endpoint work with jwt`() {
        registerUser("customer@example.com", "CUSTOMER")
        val token = login("customer@example.com")

        RestAssured.given()
            .header("Authorization", "Bearer $token")
            .`when`()
            .get("/api/v2.0/me")
            .then()
            .statusCode(200)
            .body("login", equalTo("customer@example.com"))
            .body("role", equalTo("CUSTOMER"))
            .body("id", notNullValue())
    }

    @Test
    fun `protected v2 endpoint requires authentication`() {
        RestAssured.given()
            .`when`()
            .get("/api/v2.0/creators")
            .then()
            .statusCode(401)
            .body("errorCode", equalTo(40101))
            .body("errorMessage", notNullValue())
    }

    @Test
    fun `customer cannot create tweet for another creator`() {
        registerUser("owner@example.com", "CUSTOMER")
        val otherCreatorId = registerUser("other@example.com", "CUSTOMER")
        val token = login("owner@example.com")

        RestAssured.given()
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer $token")
            .body(
                mapOf(
                    "creatorId" to otherCreatorId,
                    "title" to "Denied tweet",
                    "content" to "This should be forbidden"
                )
            )
            .`when`()
            .post("/api/v2.0/tweets")
            .then()
            .statusCode(403)
            .body("errorCode", equalTo(40301))
            .body("errorMessage", notNullValue())
    }

    @Test
    fun `admin can manage stickers while customer cannot`() {
        registerUser("admin@example.com", "ADMIN")
        registerUser("customer@example.com", "CUSTOMER")
        val adminToken = login("admin@example.com")
        val customerToken = login("customer@example.com")

        RestAssured.given()
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer $customerToken")
            .body(mapOf("name" to "forbidden-sticker"))
            .`when`()
            .post("/api/v2.0/stickers")
            .then()
            .statusCode(403)
            .body("errorCode", equalTo(40301))

        RestAssured.given()
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer $adminToken")
            .body(mapOf("name" to "allowed-sticker"))
            .`when`()
            .post("/api/v2.0/stickers")
            .then()
            .statusCode(201)
            .body("name", equalTo("allowed-sticker"))
            .body("id", notNullValue())
    }

    private fun registerUser(login: String, role: String): Long =
        RestAssured.given()
            .contentType(ContentType.JSON)
            .body(
                mapOf(
                    "login" to login,
                    "password" to "password123",
                    "firstName" to "Test",
                    "lastName" to "User",
                    "role" to role
                )
            )
            .`when`()
            .post("/api/v2.0/creators")
            .then()
            .statusCode(201)
            .extract()
            .path<Long>("id")

    private fun login(login: String): String =
        RestAssured.given()
            .contentType(ContentType.JSON)
            .body(
                mapOf(
                    "login" to login,
                    "password" to "password123"
                )
            )
            .`when`()
            .post("/api/v2.0/login")
            .then()
            .statusCode(200)
            .extract()
            .path<String>("access_token")
}
