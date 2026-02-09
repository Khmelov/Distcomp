package com.example.lab.test;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.*;
import org.springframework.http.HttpStatus;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserControllerIntegrationTest extends BaseIntegrationTest {

        private static Long userId;

        @Test
        @Order(1)
        @DisplayName("STEP 1: CREATE USER - Should return 201 Created")
        void createUser() {
                String payload = """
                                {
                                    "login": "test_user",
                                    "password": "secure123",
                                    "firstname": "John",
                                    "lastname": "Doe"
                                }
                                """;

                userId = given()
                                .baseUri(getBaseUrl())
                                .contentType(ContentType.JSON)
                                .body(payload)
                                .when()
                                .post("/api/v1.0/users")
                                .then()
                                .statusCode(HttpStatus.CREATED.value())
                                .contentType(ContentType.JSON)
                                .body("id", notNullValue())
                                .body("login", equalTo("test_user"))
                                .body("firstname", equalTo("John"))
                                .body("lastname", equalTo("Doe"))
                                .extract()
                                .jsonPath()
                                .getLong("id");

                Assertions.assertNotNull(userId);
        }

        @Test
        @Order(2)
        @DisplayName("STEP 2: GET USER BY ID - Should return 200 OK")
        void getUserById() {
                given()
                                .baseUri(getBaseUrl())
                                .when()
                                .get("/api/v1.0/users/{id}", userId)
                                .then()
                                .statusCode(HttpStatus.OK.value())
                                .contentType(ContentType.JSON)
                                .body("id", equalTo(userId.intValue()))
                                .body("login", equalTo("test_user"))
                                .body("firstname", equalTo("John"));
        }

        @Test
        @Order(3)
        @DisplayName("STEP 3: UPDATE USER - Should return 200 OK")
        void updateUser() {
                String payload = """
                                {
                                    "id": %d,
                                    "login": "test_user",
                                    "password": "newpassword456",
                                    "firstname": "Jane",
                                    "lastname": "Smith"
                                }
                                """.formatted(userId);

                given()
                                .baseUri(getBaseUrl())
                                .contentType(ContentType.JSON)
                                .body(payload)
                                .when()
                                .put("/api/v1.0/users/{id}", userId)
                                .then()
                                .statusCode(HttpStatus.OK.value())
                                .contentType(ContentType.JSON)
                                .body("id", equalTo(userId.intValue()))
                                .body("login", equalTo("test_user"))
                                .body("firstname", equalTo("Jane"))
                                .body("lastname", equalTo("Smith"));
        }

        @Test
        @Order(4)
        @DisplayName("STEP 4: DELETE USER - Should return 204 No Content")
        void deleteUser() {
                given()
                                .baseUri(getBaseUrl())
                                .when()
                                .delete("/api/v1.0/users/{id}", userId)
                                .then()
                                .statusCode(HttpStatus.NO_CONTENT.value());
        }

        @Test
        @Order(5)
        @DisplayName("STEP 5: GET DELETED USER - Should return 404 Not Found")
        void getDeletedUser() {
                given()
                                .baseUri(getBaseUrl())
                                .when()
                                .get("/api/v1.0/users/{id}", userId)
                                .then()
                                .statusCode(HttpStatus.NOT_FOUND.value())
                                .contentType(ContentType.JSON)
                                .body("errorCode", equalTo(40401));
        }
}