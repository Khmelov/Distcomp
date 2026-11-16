package by.bsuir.rest.controller;

import by.bsuir.rest.dto.request.UserRequestTo;
import by.bsuir.rest.support.ApiTestSupport;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;

class UserControllerTest extends ApiTestSupport {

    @Test
    @DisplayName("Пользователь с данными со схемы создаётся и читается")
    void createsUserFromAssignmentSample() {
        UserRequestTo request = user("levzh93@gmail.com");
        request.setFirstname("Сергей");
        request.setLastname("Залужный");
        request.setId(999_999_999L);

        int id = givenJson(request)
                .post("/users")
                .then()
                .statusCode(201)
                .contentType(ContentType.JSON)
                .body("id", greaterThan(0))
                .body("id", not(equalTo(999_999_999)))
                .body("login", equalTo("levzh93@gmail.com"))
                .body("password", equalTo("password123"))
                .body("firstname", equalTo("Сергей"))
                .body("lastname", equalTo("Залужный"))
                .extract()
                .jsonPath()
                .getInt("id");

        when().get("/users/{id}", id)
                .then()
                .statusCode(200)
                .body("login", equalTo("levzh93@gmail.com"))
                .body("firstname", equalTo("Сергей"))
                .body("lastname", equalTo("Залужный"));

        when().get("/users")
                .then()
                .statusCode(200)
                .body("id", hasItem(id))
                .body("login", hasItem("levzh93@gmail.com"));
    }

    @Test
    @DisplayName("Одинаковый login возвращает 40301, свой login при обновлении сохраняется")
    void rejectsDuplicateLogin() {
        String login = login();
        int id = createUser(login);
        assertError(givenJson(user(login)).post("/users").then(), 403, 40301);

        UserRequestTo sameLogin = user(login);
        givenJson(sameLogin)
                .put("/users/{id}", id)
                .then()
                .statusCode(200)
                .body("login", equalTo(login));

        int otherId = createUser();
        UserRequestTo taken = user(login);
        taken.setId((long) otherId);
        assertError(givenJson(taken).put("/users").then(), 403, 40301);
    }

    @Test
    @DisplayName("Пользователь обновляется по id в теле и в пути")
    void updatesUser() {
        int id = createUser();

        UserRequestTo byBody = user(login());
        byBody.setId((long) id);
        byBody.setFirstname("Ян");
        givenJson(byBody)
                .put("/users")
                .then()
                .statusCode(200)
                .body("id", equalTo(id))
                .body("firstname", equalTo("Ян"))
                .body("login", equalTo(byBody.getLogin()));

        UserRequestTo byPath = user(login());
        byPath.setLastname("Залужный");
        givenJson(byPath)
                .put("/users/{id}", id)
                .then()
                .statusCode(200)
                .body("id", equalTo(id))
                .body("lastname", equalTo("Залужный"))
                .body("login", equalTo(byPath.getLogin()));
    }

    @Test
    @DisplayName("Удаление пользователя возвращает 204, повторное чтение — 404")
    void deletesUser() {
        int id = createUser();

        when().delete("/users/{id}", id)
                .then()
                .statusCode(204);

        assertError(when().get("/users/{id}", id).then(), 404, 40401);
        assertError(when().delete("/users/{id}", id).then(), 404, 40401);
    }

    @Test
    @DisplayName("Недопустимая длина полей пользователя возвращает 40001")
    void rejectsInvalidUserFields() {
        UserRequestTo tooShort = user(login());
        tooShort.setPassword("1234567");
        tooShort.setFirstname("Я");
        assertError(givenJson(tooShort).post("/users").then(), 400, 40001);

        UserRequestTo tooLong = user("a".repeat(65));
        tooLong.setPassword("p".repeat(129));
        tooLong.setLastname("b".repeat(65));
        assertError(givenJson(tooLong).post("/users").then(), 400, 40001);

        UserRequestTo blank = user(login());
        blank.setLogin(" ");
        assertError(givenJson(blank).post("/users").then(), 400, 40001);

        UserRequestTo edges = user("ab");
        edges.setPassword("p".repeat(8));
        edges.setFirstname("Ян");
        edges.setLastname("b".repeat(64));
        givenJson(edges)
                .post("/users")
                .then()
                .statusCode(201)
                .body("firstname", equalTo("Ян"))
                .body("password", equalTo("p".repeat(8)));
    }

    @Test
    @DisplayName("Обновление без id и с разными id отклоняется")
    void rejectsBrokenUpdateId() {
        UserRequestTo missingId = user(login());
        assertError(givenJson(missingId).put("/users").then(), 400, 40003);

        UserRequestTo mismatch = user(login());
        mismatch.setId(2L);
        assertError(givenJson(mismatch).put("/users/{id}", 1).then(), 400, 40004);

        UserRequestTo missing = user(login());
        missing.setId(9_000_001L);
        assertError(givenJson(missing).put("/users").then(), 404, 40401);
    }
}
