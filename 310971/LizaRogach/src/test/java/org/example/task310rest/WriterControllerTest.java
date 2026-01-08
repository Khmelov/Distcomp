package org.example.task310rest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

public class WriterControllerTest extends RestTestBase {

    @Test
    @DisplayName("Connect Writers (GET status 200)")
    void connect() {
        given()
                .when()
                .get("/api/v1.0/writers")
                .then()
                .statusCode(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("Create and delete writer")
    void createAndDelete() {
        long id = ((Number) given().contentType(ContentType.JSON)
                .body(writerJson("login1@example.com", "password1", "Name", "Last"))
                .when().post("/api/v1.0/writers")
                .then().statusCode(HttpStatus.CREATED.value())
                .body("login", equalTo("login1@example.com"))
                .extract().path("id")).longValue();

        given().when().delete("/api/v1.0/writers/{id}", id)
                .then().statusCode(HttpStatus.NO_CONTENT.value());
    }

    @Test
    @DisplayName("Read writer")
    void readWriter() {
        long id = ((Number) given().contentType(ContentType.JSON)
                .body(writerJson("login2@example.com", "password2", "User", "Test"))
                .when().post("/api/v1.0/writers")
                .then().statusCode(HttpStatus.CREATED.value())
                .extract().path("id")).longValue();

        given().when().get("/api/v1.0/writers/{id}", id)
                .then().statusCode(HttpStatus.OK.value())
                .body("id", equalTo((int) id));

        given().when().delete("/api/v1.0/writers/{id}", id)
                .then().statusCode(HttpStatus.NO_CONTENT.value());
    }

    @Test
    @DisplayName("Read writers list")
    void readAll() {
        long id = ((Number) given().contentType(ContentType.JSON)
                .body(writerJson("login3@example.com", "password3", "Foo", "Bar"))
                .when().post("/api/v1.0/writers")
                .then().statusCode(HttpStatus.CREATED.value())
                .extract().path("id")).longValue();

        given().when().get("/api/v1.0/writers")
                .then().statusCode(HttpStatus.OK.value())
                .body("size()", notNullValue());

        given().when().delete("/api/v1.0/writers/{id}", id)
                .then().statusCode(HttpStatus.NO_CONTENT.value());
    }

    @Test
    @DisplayName("Update writer")
    void updateWriter() {
        long id = ((Number) given().contentType(ContentType.JSON)
                .body(writerJson("login4@example.com", "password4", "Anna", "Bee"))
                .when().post("/api/v1.0/writers")
                .then().statusCode(HttpStatus.CREATED.value())
                .extract().path("id")).longValue();

        given().contentType(ContentType.JSON)
                .body(writerJson("updated@example.com", "password4", "Ann", "Bee"))
                .when().put("/api/v1.0/writers/{id}", id)
                .then().statusCode(HttpStatus.OK.value())
                .body("login", equalTo("updated@example.com"));

        given().when().delete("/api/v1.0/writers/{id}", id)
                .then().statusCode(HttpStatus.NO_CONTENT.value());
    }

    @Test
    @DisplayName("Delete writer and error on second delete")
    void deleteWriterTwice() {
        long id = ((Number) given().contentType(ContentType.JSON)
                .body(writerJson("login5@example.com", "password5", "Carl", "Doe"))
                .when().post("/api/v1.0/writers")
                .then().statusCode(HttpStatus.CREATED.value())
                .extract().path("id")).longValue();

        given().when().delete("/api/v1.0/writers/{id}", id)
                .then().statusCode(HttpStatus.NO_CONTENT.value());

        given().when().delete("/api/v1.0/writers/{id}", id)
                .then().statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @DisplayName("Error update writer not found")
    void errorUpdateNotFound() {
        given().contentType(ContentType.JSON)
                .body(writerJson("login6@example.com", "password6", "El", "Fo"))
                .when().put("/api/v1.0/writers/{id}", 9999)
                .then().statusCode(HttpStatus.NOT_FOUND.value());
    }

    private String writerJson(String login, String password, String firstname, String lastname) {
        return """
                {
                  "login": "%s",
                  "password": "%s",
                  "firstname": "%s",
                  "lastname": "%s"
                }
                """.formatted(login, password, firstname, lastname);
    }
}


