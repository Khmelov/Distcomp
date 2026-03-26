package com.example.demo;

import com.example.labrest.dto.CreatorRequestTo;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ControllerTest {
    @LocalServerPort private int port;

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";
    }

    @Test
    public void testCreateCreator() {
        CreatorRequestTo req = new CreatorRequestTo("test@test.com", "password123", "John", "Doe");
        given().contentType(ContentType.JSON).body(req).when().post("/api/v1.0/creators")
                .then().statusCode(201).body("login", equalTo("test@test.com"));
    }

    @Test
    public void testGetCreators() {
        given().when().get("/api/v1.0/creators").then().statusCode(200).body("size()", greaterThan(0));
    }

    @Test
    public void testValidationFail() {
        CreatorRequestTo req = new CreatorRequestTo("a", "pass", "A", "B");
        given().contentType(ContentType.JSON).body(req).when().post("/api/v1.0/creators")
                .then().statusCode(400);
    }
}