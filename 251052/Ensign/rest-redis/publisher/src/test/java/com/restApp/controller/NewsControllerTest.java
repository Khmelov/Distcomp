package com.restApp.controller;

import com.restApp.AbstractIntegrationTest;
import com.restApp.dto.AuthorRequestTo;
import com.restApp.dto.NewsRequestTo;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

class NewsControllerTest extends AbstractIntegrationTest {

        private Long createAuthor() {
                AuthorRequestTo request = new AuthorRequestTo();
                request.setLogin("news_" + System.currentTimeMillis());
                request.setPassword("password");
                request.setFirstname("News");
                request.setLastname("Maker");

                return given()
                                .log().all()
                                .contentType(ContentType.JSON)
                                .body(request)
                                .post("/authors")
                                .then()
                                .log().all()
                                .statusCode(201)
                                .extract().jsonPath().getLong("id");
        }

        @Test
        void testCRUD() {
                Long authorId = createAuthor();

                NewsRequestTo request = new NewsRequestTo();
                request.setTitle("Breaking News");
                request.setContent("Something happened today of importance.");
                request.setAuthorId(authorId);

                // Create
                Integer newsId = given()
                                .log().all()
                                .contentType(ContentType.JSON)
                                .body(request)
                                .when()
                                .post("/news")
                                .then()
                                .log().all()
                                .statusCode(201)
                                .body("title", equalTo("Breaking News"))
                                .extract().path("id");

                // Read
                given()
                                .when()
                                .get("/news/" + newsId)
                                .then()
                                .statusCode(200)
                                .body("content", equalTo("Something happened today of importance."));

                // Update
                request.setTitle("Updated News");
                given()
                                .contentType(ContentType.JSON)
                                .body(request)
                                .when()
                                .put("/news/" + newsId)
                                .then()
                                .statusCode(200)
                                .body("title", equalTo("Updated News"));

                // Delete
                given()
                                .when()
                                .delete("/news/" + newsId)
                                .then()
                                .statusCode(204);

                // Verify 404
                given()
                                .when()
                                .get("/news/" + newsId)
                                .then()
                                .statusCode(404);
        }
}
