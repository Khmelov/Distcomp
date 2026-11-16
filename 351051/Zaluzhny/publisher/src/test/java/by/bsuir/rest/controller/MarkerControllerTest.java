package by.bsuir.rest.controller;

import by.bsuir.rest.dto.request.MarkerRequestTo;
import by.bsuir.rest.support.ApiTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;

class MarkerControllerTest extends ApiTestSupport {

    @Test
    @DisplayName("Маркер создаётся, находится в списке, обновляется и удаляется")
    void crudMarker() {
        String name = markerName();
        int id = createMarker(name);

        when().get("/markers/{id}", id)
                .then()
                .statusCode(200)
                .body("id", equalTo(id))
                .body("name", equalTo(name));

        when().get("/markers")
                .then()
                .statusCode(200)
                .body("id", hasItem(id));

        MarkerRequestTo update = new MarkerRequestTo();
        update.setId((long) id);
        update.setName(markerName());
        givenJson(update)
                .put("/markers/{id}", id)
                .then()
                .statusCode(200)
                .body("id", equalTo(id))
                .body("name", equalTo(update.getName()));

        when().delete("/markers/{id}", id)
                .then()
                .statusCode(204);

        assertError(when().get("/markers/{id}", id).then(), 404, 40401);
    }

    @Test
    @DisplayName("Имя маркера уникально, длина от 2 до 32")
    void validatesMarkerName() {
        MarkerRequestTo tooShort = new MarkerRequestTo();
        tooShort.setName("a");
        assertError(givenJson(tooShort).post("/markers").then(), 400, 40001);

        MarkerRequestTo tooLong = new MarkerRequestTo();
        tooLong.setName("n".repeat(33));
        assertError(givenJson(tooLong).post("/markers").then(), 400, 40001);

        String name = markerName();
        int id = createMarker(name);

        MarkerRequestTo duplicate = new MarkerRequestTo();
        duplicate.setName(name);
        assertError(givenJson(duplicate).post("/markers").then(), 403, 40301);

        MarkerRequestTo sameName = new MarkerRequestTo();
        sameName.setName(name);
        givenJson(sameName)
                .put("/markers/{id}", id)
                .then()
                .statusCode(200)
                .body("name", equalTo(name));

        int otherId = createMarker();
        MarkerRequestTo taken = new MarkerRequestTo();
        taken.setName(name);
        assertError(givenJson(taken).put("/markers/{id}", otherId).then(), 403, 40301);

        MarkerRequestTo maxLength = new MarkerRequestTo();
        maxLength.setName("m".repeat(32));
        givenJson(maxLength)
                .post("/markers")
                .then()
                .statusCode(201)
                .body("name", equalTo(maxLength.getName()));
    }

    @Test
    @DisplayName("Чтение и удаление отсутствующего маркера возвращают 404")
    void missingMarker() {
        assertError(when().get("/markers/{id}", 9_000_002).then(), 404, 40401);
        assertError(when().delete("/markers/{id}", 9_000_002).then(), 404, 40401);
    }
}
