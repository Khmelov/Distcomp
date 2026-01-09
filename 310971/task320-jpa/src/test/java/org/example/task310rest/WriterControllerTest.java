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

    @Test
    @DisplayName("Check CREATE with non-valid fields Writer")
    void createWithInvalidFields() {
        // Test with empty login
        given().contentType(ContentType.JSON)
                .body(writerJson("", "password123", "First", "Last"))
                .when().post("/api/v1.0/writers")
                .then().statusCode(HttpStatus.BAD_REQUEST.value());

        // Test with short password
        given().contentType(ContentType.JSON)
                .body(writerJson("test@example.com", "short", "First", "Last"))
                .when().post("/api/v1.0/writers")
                .then().statusCode(HttpStatus.BAD_REQUEST.value());

        // Test with short firstname
        given().contentType(ContentType.JSON)
                .body(writerJson("test@example.com", "password123", "A", "Last"))
                .when().post("/api/v1.0/writers")
                .then().statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("Check 403 CREATE with duplicate login Writer")
    void createWithDuplicateLogin() {
        String login = "duplicate@example.com";
        long id = ((Number) given().contentType(ContentType.JSON)
                .body(writerJson(login, "password123", "First", "Last"))
                .when().post("/api/v1.0/writers")
                .then().statusCode(HttpStatus.CREATED.value())
                .extract().path("id")).longValue();

        // Try to create another writer with same login
        given().contentType(ContentType.JSON)
                .body(writerJson(login, "password456", "Second", "Writer"))
                .when().post("/api/v1.0/writers")
                .then().statusCode(HttpStatus.FORBIDDEN.value());

        // Cleanup
        given().when().delete("/api/v1.0/writers/{id}", id)
                .then().statusCode(HttpStatus.NO_CONTENT.value());
    }

    @Test
    @DisplayName("Regression check REST Writer (Repeat all CRUD)")
    void regressionCheckAllCrud() {
        String login = "regression@example.com";
        String password = "password123";
        String firstname = "Regression";
        String lastname = "Test";
        
        // CREATE
        long id = ((Number) given().contentType(ContentType.JSON)
                .body(writerJson(login, password, firstname, lastname))
                .when().post("/api/v1.0/writers")
                .then().statusCode(HttpStatus.CREATED.value())
                .body("login", equalTo(login))
                .body("firstname", equalTo(firstname))
                .body("lastname", equalTo(lastname))
                .extract().path("id")).longValue();

        // READ by ID
        given().when().get("/api/v1.0/writers/{id}", id)
                .then().statusCode(HttpStatus.OK.value())
                .body("id", equalTo((int) id))
                .body("login", equalTo(login));

        // READ ALL
        given().when().get("/api/v1.0/writers")
                .then().statusCode(HttpStatus.OK.value())
                .body("size()", notNullValue());

        // UPDATE
        String updatedLogin = "updated-regression@example.com";
        given().contentType(ContentType.JSON)
                .body(writerJson(updatedLogin, password, firstname, lastname))
                .when().put("/api/v1.0/writers/{id}", id)
                .then().statusCode(HttpStatus.OK.value())
                .body("login", equalTo(updatedLogin));

        // DELETE
        given().when().delete("/api/v1.0/writers/{id}", id)
                .then().statusCode(HttpStatus.NO_CONTENT.value());

        // Verify deletion
        given().when().get("/api/v1.0/writers/{id}", id)
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


