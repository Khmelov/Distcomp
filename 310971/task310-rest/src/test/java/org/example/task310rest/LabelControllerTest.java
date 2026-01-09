package org.example.task310rest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.http.HttpStatus;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class LabelControllerTest extends RestTestBase {

    private Long writerId;
    private Long tweetId;

    @Test
    @DisplayName("Connect Labels (GET status 200)")
    void connect() {
        ensureRefs();
        given().when().get("/api/v1.0/labels")
                .then().statusCode(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("Create and delete label")
    void createAndDelete() {
        ensureRefs();
        long id = createLabel("lbl1");
        deleteLabel(id);
    }

    @Test
    @DisplayName("Read label")
    void readLabel() {
        ensureRefs();
        long id = createLabel("lbl2");
        given().when().get("/api/v1.0/labels/{id}", id)
                .then().statusCode(HttpStatus.OK.value())
                .body("id", equalTo((int) id));
        deleteLabel(id);
    }

    @Test
    @DisplayName("Read labels list")
    void readAll() {
        ensureRefs();
        long id = createLabel("lbl3");
        given().when().get("/api/v1.0/labels")
                .then().statusCode(HttpStatus.OK.value())
                .body("size()", notNullValue());
        deleteLabel(id);
    }

    @Test
    @DisplayName("Update label")
    void updateLabel() {
        ensureRefs();
        long id = createLabel("lbl4");
        given().contentType(ContentType.JSON)
                .body(labelJson("lbl4-upd"))
                .when().put("/api/v1.0/labels/{id}", id)
                .then().statusCode(HttpStatus.OK.value())
                .body("name", equalTo("lbl4-upd"));
        deleteLabel(id);
    }

    @Test
    @DisplayName("Delete label and error on second delete")
    void deleteTwice() {
        ensureRefs();
        long id = createLabel("lbl5");
        deleteLabel(id);
        given().when().delete("/api/v1.0/labels/{id}", id)
                .then().statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @DisplayName("Error update label not found")
    void errorUpdateNotFound() {
        ensureRefs();
        given().contentType(ContentType.JSON)
                .body(labelJson("lbl6"))
                .when().put("/api/v1.0/labels/{id}", 99999)
                .then().statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @DisplayName("Check 4xx CREATE with non-valid fields Label")
    void createWithInvalidFields() {
        ensureRefs();
        
        // Test with empty name
        given().contentType(ContentType.JSON)
                .body("""
                        {
                          "name": ""
                        }
                        """)
                .when().post("/api/v1.0/labels")
                .then().statusCode(HttpStatus.BAD_REQUEST.value());

        // Test with short name
        given().contentType(ContentType.JSON)
                .body("""
                        {
                          "name": "A"
                        }
                        """)
                .when().post("/api/v1.0/labels")
                .then().statusCode(HttpStatus.BAD_REQUEST.value());

        // Test with long name
        given().contentType(ContentType.JSON)
                .body("""
                        {
                          "name": "This is a very long label name that exceeds the maximum length of 32 characters"
                        }
                        """)
                .when().post("/api/v1.0/labels")
                .then().statusCode(HttpStatus.BAD_REQUEST.value());
    }

    private void ensureRefs() {
        if (writerId == null) {
            writerId = ((Number) given().contentType(ContentType.JSON)
                    .body("""
                            {"login":"labelwriter@example.com","password":"password1","firstname":"Label","lastname":"Writer"}
                            """)
                    .when().post("/api/v1.0/writers")
                    .then().statusCode(HttpStatus.CREATED.value())
                    .extract().path("id")).longValue();
        }
        if (tweetId == null) {
            tweetId = ((Number) given().contentType(ContentType.JSON)
                    .body("""
                            {"writerId":%d,"title":"Label title","content":"Label content"}
                            """.formatted(writerId))
                    .when().post("/api/v1.0/tweets")
                    .then().statusCode(HttpStatus.CREATED.value())
                    .extract().path("id")).longValue();
        }
    }

    private long createLabel(String name) {
        return ((Number) given().contentType(ContentType.JSON)
                .body(labelJson(name))
                .when().post("/api/v1.0/labels")
                .then().statusCode(HttpStatus.CREATED.value())
                .extract().path("id")).longValue();
    }

    private void deleteLabel(Long id) {
        given().when().delete("/api/v1.0/labels/{id}", id)
                .then().statusCode(HttpStatus.NO_CONTENT.value());
    }

    private String labelJson(String name) {
        return """
                {"name":"%s"}
                """.formatted(name);
    }
}


