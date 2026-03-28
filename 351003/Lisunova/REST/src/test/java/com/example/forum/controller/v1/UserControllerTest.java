package com.example.forum.controller.v1;

import com.example.forum.RestForumApplication;
import com.example.forum.repository.UserRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import static org.hamcrest.Matchers.*;

@SpringBootTest(classes = RestForumApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class UserControllerTest {

    @BeforeAll
    static void setup() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 24110;
    }

    @Autowired
    UserRepository userRepository;

    @BeforeEach
    void clean() {
        userRepository.clear();
    }


    @Test
    void testCreateUser() {
        RestAssured.given()
                .contentType(ContentType.JSON)
                .body("""
                        {
                          "login": "test@example.com",
                          "password": "password123",
                          "firstname": "Test",
                          "lastname": "User"
                        }
                        """)
                .when()
                .post("/api/v1.0/users")
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("login", equalTo("test@example.com"));
    }

    @Test
    void testGetUsers() {
        RestAssured.given()
                .when()
                .get("/api/v1.0/users")
                .then()
                .statusCode(200)
                .body("$", notNullValue());
    }

    @Test
    void testGetUserById() {
        // создаём пользователя
        Integer id = RestAssured.given()
                .contentType(ContentType.JSON)
                .body("""
                    {
                      "login": "idtest@example.com",
                      "password": "password123",
                      "firstname": "Ida",
                      "lastname": "Test"
                    }
                    """)
                .when()
                .post("/api/v1.0/users")
                .then()
                .statusCode(201)
                .extract()
                .path("id");

        // проверяем, что id не null
        assertNotNull(id, "User was not created, id is null");

        // проверяем получение
        RestAssured.given()
                .when()
                .get("/api/v1.0/users/" + id)
                .then()
                .statusCode(200)
                .body("id", equalTo(id));
    }

}
