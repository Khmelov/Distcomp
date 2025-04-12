package by.molchan.Publisher;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static io.restassured.RestAssured.given;


public class CreatorsTest extends FunctionalTest {
    @Test
    public void getAllCreators() {
        given().when().get("/creators").then().statusCode(200);
    }

    private Map<String, String> createCreator() {
        Map<String, String> creator = new HashMap<>();
        Random random = new Random();
        creator.put("login", "login" + random.nextInt(10000));
        creator.put("password", "asdfghj1678");
        creator.put("firstname", "asdfdsufhsdif");
        creator.put("lastname", "asdfduighd");
        return creator;
    }

    @Test
    public void createDuplicateCreator() {
        var creator = createCreator();
        given()
                .contentType("application/json")
                .body(creator)
                .when().post("/creators").then()
                .statusCode(201);

        given()
                .contentType("application/json")
                .body(creator)
                .when().post("/creators").then()
                .statusCode(400);
    }

    @Test
    public void createAndUpdateWithInvalidData() {
        var creator = createCreator();

        var creatorWithIncorrectPassword = new HashMap<>(creator);
        creatorWithIncorrectPassword.put("password", "asd"); // слишком короткий пароль

        var creatorWithIncorrectName = new HashMap<>(creator);
        creatorWithIncorrectName.put("firstname", " "); // пустое имя

        // 🔴 Попытка создать пользователя с некорректным паролем
        given()
                .contentType("application/json")
                .body(creatorWithIncorrectPassword)
                .when().post("/creators")
                .then()
                .statusCode(400);
        
        given()
                .contentType("application/json")
                .body(creatorWithIncorrectName)
                .when().post("/creators")
                .then()
                .statusCode(400);


        Integer id = given()
                .contentType("application/json")
                .body(creator)
                .when().post("/creators")
                .then()
                .statusCode(201)
                .extract()
                .path("id");

        creator.put("id", id.toString());
        creatorWithIncorrectPassword.put("id", id.toString());
        creatorWithIncorrectName.put("id", id.toString());

        given()
                .contentType("application/json")
                .body(creatorWithIncorrectPassword)
                .when().put("/creators/" + id)
                .then()
                .statusCode(400);

        given()
                .contentType("application/json")
                .body(creatorWithIncorrectName)
                .when().put("/creators/" + id)
                .then()
                .statusCode(400);


        creator.put("password", "CorrectPassword123!");
        creator.put("firstname", "ValidName");

        given()
                .contentType("application/json")
                .body(creator)
                .when().put("/creators/" + id)
                .then()
                .statusCode(200);
    }

}
