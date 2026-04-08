package com.example.lab.test;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.*;
import org.springframework.http.HttpStatus;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class NewsControllerIntegrationTest extends BaseIntegrationTest {

        private static Long newsId;
        private static Long userId;

        @BeforeEach
        void createTestUser() {
                // Создаем пользователя для новости
                String userPayload = """
                                {
                                    "login": "news_author",
                                    "password": "password123",
                                    "firstname": "Author",
                                    "lastname": "Name"
                                }
                                """;

                userId = given()
                                .baseUri(getBaseUrl())
                                .contentType(ContentType.JSON)
                                .body(userPayload)
                                .when()
                                .post("/api/v1.0/users")
                                .then()
                                .statusCode(HttpStatus.CREATED.value())
                                .extract()
                                .jsonPath()
                                .getLong("id");
        }

        @Test
        @Order(1)
        @DisplayName("STEP 1: CREATE NEWS - Should return 201 Created")
        void createNews() {
                String payload = """
                                {
                                    "title": "Breaking News",
                                    "content": "This is important news!",
                                    "userId": %d
                                }
                                """.formatted(userId);

                newsId = given()
                                .baseUri(getBaseUrl())
                                .contentType(ContentType.JSON)
                                .body(payload)
                                .when()
                                .post("/api/v1.0/news")
                                .then()
                                .statusCode(HttpStatus.CREATED.value())
                                .contentType(ContentType.JSON)
                                .body("id", notNullValue())
                                .body("title", equalTo("Breaking News"))
                                .body("content", equalTo("This is important news!"))
                                .extract()
                                .jsonPath()
                                .getLong("id");

                Assertions.assertNotNull(newsId);
        }

        @Test
        @Order(2)
        @DisplayName("STEP 2: GET NEWS BY ID - Should return 200 OK")
        void getNewsById() {
                given()
                                .baseUri(getBaseUrl())
                                .when()
                                .get("/api/v1.0/news/{id}", newsId)
                                .then()
                                .statusCode(HttpStatus.OK.value())
                                .contentType(ContentType.JSON)
                                .body("id", equalTo(newsId.intValue()))
                                .body("title", equalTo("Breaking News"))
                                .body("content", equalTo("This is important news!"));
        }

        @Test
        @Order(4)
        @DisplayName("STEP 4: UPDATE NEWS - Should return 200 OK")
        void updateNews() {
                String payload = """
                                {
                                    "id": %d,
                                    "title": "Updated News",
                                    "content": "Updated content!",
                                    "userId": %d
                                }
                                """.formatted(newsId, userId);

                given()
                                .baseUri(getBaseUrl())
                                .contentType(ContentType.JSON)
                                .body(payload)
                                .when()
                                .put("/api/v1.0/news/{id}", newsId)
                                .then()
                                .statusCode(HttpStatus.OK.value())
                                .contentType(ContentType.JSON)
                                .body("id", equalTo(newsId.intValue()))
                                .body("title", equalTo("Updated News"))
                                .body("content", equalTo("Updated content!"));
        }

        @Test
        @Order(5)
        @DisplayName("STEP 5: DELETE NEWS - Should return 204 No Content")
        void deleteNews() {
                given()
                                .baseUri(getBaseUrl())
                                .when()
                                .delete("/api/v1.0/news/{id}", newsId)
                                .then()
                                .statusCode(HttpStatus.NO_CONTENT.value());
        }

        @Test
        @Order(6)
        @DisplayName("STEP 6: GET DELETED NEWS - Should return 404 Not Found")
        void getDeletedNews() {
                given()
                                .baseUri(getBaseUrl())
                                .when()
                                .get("/api/v1.0/news/{id}", newsId)
                                .then()
                                .statusCode(HttpStatus.NOT_FOUND.value())
                                .contentType(ContentType.JSON)
                                .body("errorCode", equalTo(40401));
        }

        @Test
        @Order(7)
        @DisplayName("STEP 7: GET USER BY DELETED NEWS ID - Should return 404 Not Found")
        void getUserByDeletedNewsId() {
                given()
                                .baseUri(getBaseUrl())
                                .when()
                                .get("/api/v1.0/news/user/{id}", newsId)
                                .then()
                                .statusCode(HttpStatus.NOT_FOUND.value())
                                .body("errorCode", equalTo(40401));
        }

        @Test
        @Order(8)
        @DisplayName("NEGATIVE: CREATE NEWS WITH INVALID USER ID - Should return 404 Not Found")
        void createNewsWithInvalidUserId() {
                String payload = """
                                {
                                    "title": "Invalid News",
                                    "content": "This should fail",
                                    "userId": 999999
                                }
                                """;

                given()
                                .baseUri(getBaseUrl())
                                .contentType(ContentType.JSON)
                                .body(payload)
                                .when()
                                .post("/api/v1.0/news")
                                .then()
                                .statusCode(HttpStatus.NOT_FOUND.value())
                                .body("errorCode", equalTo(40401));
        }
}