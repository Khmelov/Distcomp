package com.github.Lexya06.startrestapp;

import com.github.Lexya06.startrestapp.controller.error.ErrorDescription;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;


class UserRestTest extends FunctionalTest {
    private Integer userId;
    final HashMap<String,String> startBody;
    public UserRestTest() {
        startBody = new HashMap<>();
        startBody.put("login", "admin");
        startBody.put("password", "adminnio");
        startBody.put("firstname", "Yana");
        startBody.put("lastname", "Lexya");
    }
    @BeforeEach
    public void verifyCreateUser(){
        userId = given().contentType("application/json").body(startBody).when().post("/users").then()
                        .statusCode(HttpStatus.SC_CREATED).extract().path("id");

    }

    @Test
    void verifyUpdateUser(){
        HashMap<String,String> body = new HashMap<>();
        body.put("login", "adminn");
        body.put("password", "adminnio");
        body.put("firstname", "Yana");
        body.put("lastname", "Lexyaa");
        given().contentType("application/json").body(body).when().put("/users/"+userId).then().
                statusCode(HttpStatus.SC_OK) .body("id", equalTo(userId));
    }

    @Test
    void verifyDeleteUser(){
        given().when().delete("/users/"+userId).then().statusCode(HttpStatus.SC_NO_CONTENT);
    }

    @Test
    void verifyGetAllUsers(){
        given().when().get("/users").then().statusCode(HttpStatus.SC_OK)
                        .body("size()", equalTo(1))
                        .body("[0].id", equalTo(userId));
    }

    @Test
    void verifyGetUser(){
        given().when().get("/users/"+userId).then().statusCode(HttpStatus.SC_OK).body("id", equalTo(userId));
    }

    @Test
    void verifyUserNotFound(){
        given().when().get("/users/2").then().statusCode(ErrorDescription.ENTITY_NOT_FOUND.getStatus().value());
    }

    @Test
    void verifyUserNotCreated(){
        HashMap<String,String> body = new HashMap<>();
        body.put("login", "adminn");
        body.put("password", "a");
        body.put("firstname", "Y");
        body.put("lastname", "L");
        given().contentType("application/json").body(body).when().post("/users").then()
                .statusCode(ErrorDescription.BAD_REQUEST_BODY.getStatus().value());
    }







}
