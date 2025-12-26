package com.restApp.controller;

import com.restApp.AbstractIntegrationTest;
import com.restApp.dto.AuthorRequestTo;
import com.restApp.dto.CommentRequestTo;
import com.restApp.dto.NewsRequestTo;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

class CommentControllerTest extends AbstractIntegrationTest {

        private Long createAuthor() {
                AuthorRequestTo request = new AuthorRequestTo();
                request.setLogin("comm_" + System.currentTimeMillis());
                request.setPassword("password");
                request.setFirstname("Comment");
                request.setLastname("Poster");

                return given()
                                .contentType(ContentType.JSON)
                                .body(request)
                                .post("/authors")
                                .then()
                                .statusCode(201)
                                .extract().jsonPath().getLong("id");
        }

        private Long createNews(Long authorId) {
                NewsRequestTo request = new NewsRequestTo();
                request.setTitle("Commentable News");
                request.setContent("This news creates comments.");
                request.setAuthorId(authorId);

                return given()
                                .contentType(ContentType.JSON)
                                .body(request)
                                .post("/news")
                                .then()
                                .statusCode(201)
                                .extract().jsonPath().getLong("id");
        }

        @Test
        void testCRUD() {
                Long authorId = createAuthor();
                Long newsId = createNews(authorId);

                CommentRequestTo request = new CommentRequestTo();
                request.setContent("Great article!");
                request.setNewsId(newsId);

                // Create
                Integer id = given()
                                .contentType(ContentType.JSON)
                                .body(request)
                                .when()
                                .post("/comments")
                                .then()
                                .statusCode(201)
                                .body("content", equalTo("Great article!"))
                                .extract().path("id");

                // Read
                given()
                                .when()
                                .get("/comments/" + id)
                                .then()
                                .statusCode(200)
                                .body("content", equalTo("Great article!"));

                // Update
                request.setContent("Updated comment");
                given()
                                .contentType(ContentType.JSON)
                                .body(request)
                                .when()
                                .put("/comments/" + id)
                                .then()
                                .statusCode(200)
                                .body("content", equalTo("Updated comment"));

                // Delete
                given()
                                .when()
                                .delete("/comments/" + id)
                                .then()
                                .statusCode(204);
        }
}
