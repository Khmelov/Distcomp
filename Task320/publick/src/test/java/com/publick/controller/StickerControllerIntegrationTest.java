package com.publick.controller;

import com.publick.AbstractIntegrationTest;
import com.publick.dto.StickerRequestTo;
import com.publick.dto.StickerResponseTo;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class StickerControllerIntegrationTest extends AbstractIntegrationTest {

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";
    }

    @Test
    void getAllStickers_ShouldReturnList() {
        given()
            .contentType(ContentType.JSON)
        .when()
            .get("/api/v1.0/stickers")
        .then()
            .statusCode(200)
            .body("$", notNullValue());
    }

    @Test
    void createSticker_ShouldReturnCreatedSticker() {
        StickerRequestTo request = new StickerRequestTo();
        request.setName("Test Sticker");

        given()
            .contentType(ContentType.JSON)
            .body(request)
        .when()
            .post("/api/v1.0/stickers")
        .then()
            .statusCode(201)
            .body("name", equalTo("Test Sticker"));
    }

    @Test
    void getStickerById_ShouldReturnSticker_WhenExists() {
        // First create a sticker
        StickerRequestTo request = new StickerRequestTo();
        request.setName("Get Sticker Test");

        Long stickerId = given()
            .contentType(ContentType.JSON)
            .body(request)
        .when()
            .post("/api/v1.0/stickers")
        .then()
            .statusCode(201)
            .extract()
            .path("id");

        // Then get it by id
        given()
            .contentType(ContentType.JSON)
        .when()
            .get("/api/v1.0/stickers/{id}", stickerId)
        .then()
            .statusCode(200)
            .body("id", equalTo(stickerId.intValue()))
            .body("name", equalTo("Get Sticker Test"));
    }

    @Test
    void updateSticker_ShouldReturnUpdatedSticker() {
        // First create a sticker
        StickerRequestTo createRequest = new StickerRequestTo();
        createRequest.setName("Update Sticker Test");

        Long stickerId = given()
            .contentType(ContentType.JSON)
            .body(createRequest)
        .when()
            .post("/api/v1.0/stickers")
        .then()
            .statusCode(201)
            .extract()
            .path("id");

        // Then update it
        StickerRequestTo updateRequest = new StickerRequestTo();
        updateRequest.setName("Updated Sticker Test");

        given()
            .contentType(ContentType.JSON)
            .body(updateRequest)
        .when()
            .put("/api/v1.0/stickers/{id}", stickerId)
        .then()
            .statusCode(200)
            .body("id", equalTo(stickerId.intValue()))
            .body("name", equalTo("Updated Sticker Test"));
    }

    @Test
    void deleteSticker_ShouldReturnNoContent() {
        // First create a sticker
        StickerRequestTo request = new StickerRequestTo();
        request.setName("Delete Sticker Test");

        Long stickerId = given()
            .contentType(ContentType.JSON)
            .body(request)
        .when()
            .post("/api/v1.0/stickers")
        .then()
            .statusCode(201)
            .extract()
            .path("id");

        // Then delete it
        given()
            .contentType(ContentType.JSON)
        .when()
            .delete("/api/v1.0/stickers/{id}", stickerId)
        .then()
            .statusCode(204);
    }

    @Test
    void createSticker_ShouldReturn400_WhenInvalidData() {
        StickerRequestTo request = new StickerRequestTo();
        request.setName("A"); // Too short

        given()
            .contentType(ContentType.JSON)
            .body(request)
        .when()
            .post("/api/v1.0/stickers")
        .then()
            .statusCode(400);
    }
}