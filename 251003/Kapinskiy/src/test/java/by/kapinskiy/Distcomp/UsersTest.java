package by.kapinskiy.Distcomp;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static io.restassured.RestAssured.given;


public class UsersTest extends FunctionalTest {
    @Test
    public void getAllUsers() {
        given().when().get("/users").then().statusCode(200);
    }

    private Map<String, String> createUser() {
        Map<String, String> user = new HashMap<>();
        Random random = new Random();
        user.put("login", "login" + random.nextInt(10000));
        user.put("password", "asdfghj1678");
        user.put("firstname", "asdfdsufhsdif");
        user.put("lastname", "asdfduighd");
        return user;
    }

    @Test
    public void createDuplicateUser() {
        var user = createUser();
        given()
                .contentType("application/json")
                .body(user)
                .when().post("/users").then()
                .statusCode(201);

        given()
                .contentType("application/json")
                .body(user)
                .when().post("/users").then()
                .statusCode(400);
    }

    @Test
    public void createAndUpdateWithInvalidData() {
        var user = createUser();

        var userWithIncorrectPassword = new HashMap<>(user);
        userWithIncorrectPassword.put("password", "asd"); // слишком короткий пароль

        var userWithIncorrectName = new HashMap<>(user);
        userWithIncorrectName.put("firstname", " "); // пустое имя

        // 🔴 Попытка создать пользователя с некорректным паролем
        given()
                .contentType("application/json")
                .body(userWithIncorrectPassword)
                .when().post("/users")
                .then()
                .statusCode(400);
        
        given()
                .contentType("application/json")
                .body(userWithIncorrectName)
                .when().post("/users")
                .then()
                .statusCode(400);


        Integer id = given()
                .contentType("application/json")
                .body(user)
                .when().post("/users")
                .then()
                .statusCode(201)
                .extract()
                .path("id");

        user.put("id", id.toString());
        userWithIncorrectPassword.put("id", id.toString());
        userWithIncorrectName.put("id", id.toString());

        given()
                .contentType("application/json")
                .body(userWithIncorrectPassword)
                .when().put("/users/" + id)
                .then()
                .statusCode(400);

        given()
                .contentType("application/json")
                .body(userWithIncorrectName)
                .when().put("/users/" + id)
                .then()
                .statusCode(400);


        user.put("password", "CorrectPassword123!");
        user.put("firstname", "ValidName");

        given()
                .contentType("application/json")
                .body(user)
                .when().put("/users/" + id)
                .then()
                .statusCode(200);
    }

}
