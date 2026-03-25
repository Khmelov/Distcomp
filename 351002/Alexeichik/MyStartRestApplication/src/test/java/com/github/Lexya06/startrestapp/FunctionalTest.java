package com.github.Lexya06.startrestapp;

import com.github.Lexya06.startrestapp.DataTestBuilder.DataTestBuilder;
import com.github.Lexya06.startrestapp.controller.error.ErrorDescription;
import io.restassured.RestAssured;
import jakarta.annotation.PostConstruct;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract public class FunctionalTest {
    private String entitiesPath;
    private String contentType;
    private String idName;


    abstract protected DataTestBuilder getDataTestBuilder();


    protected Integer entityId;


    @PostConstruct
    public void init(){
        this.entitiesPath = getDataTestBuilder().getEntitiesPath();
        this.contentType = getDataTestBuilder().getContentType();
        this.idName = getDataTestBuilder().getIdName();
    }
    @Autowired
    private Environment env;

    @BeforeAll
    public void setup() {
        String port = env.getProperty("server.port");
        if (port == null) {
            RestAssured.port = 8080;
        }
        else{
            RestAssured.port = Integer.parseInt(port);
        }


        String basePath = env.getProperty("server.api.base-path.v1");
        if(basePath==null){
            basePath = "/api/v1.0";
        }
        RestAssured.basePath = basePath;

        String baseHost = env.getProperty("server.address");
        if(baseHost==null){
            baseHost = "http://localhost";
        }
        else
            baseHost = "http://" + baseHost;
        RestAssured.baseURI = baseHost;

    }
    protected Integer setupTest() {
        return getDataTestBuilder().getCreatedEntityId();
    }

    @BeforeEach
    protected void verifyCreateEntity() {
        entityId = setupTest();

    }

    @Test
    protected void verifyUpdateEntity(){
        given().contentType(contentType).body(getDataTestBuilder().getUpdateBody()).when().put(getDataTestBuilder().getEntityPath(entityId)).then().
                statusCode(HttpStatus.SC_OK) .body(idName, equalTo(entityId));
    }

    @Test
    protected void verifyDeleteEntity(){
        given().when().delete(getDataTestBuilder().getEntityPath(entityId)).then().statusCode(HttpStatus.SC_NO_CONTENT);
    }

    @Test
    protected void verifyGetAllEntities(){
        given().when().get(entitiesPath).then().statusCode(HttpStatus.SC_OK)
                .body("size()", equalTo(1))
                .body("[0]."+ idName, equalTo(entityId));
    }

    @Test
    protected void verifyGetEntityById(){
        given().when().get(getDataTestBuilder().getEntityPath(entityId)).then().statusCode(HttpStatus.SC_OK).body(idName, equalTo(entityId));
    }

    @Test
    protected void verifyEntityNotFound(){
        given().when().get(getDataTestBuilder().getEntityPath(entityId + 1)).then().statusCode(ErrorDescription.ENTITY_NOT_FOUND.getStatus().value());
    }

    @Test
    protected void verifyEntityNotCreated(){
        given().contentType(contentType).body(getDataTestBuilder().getInvalidBody()).when().post(entitiesPath).then()
                .statusCode(ErrorDescription.BAD_REQUEST_BODY.getStatus().value());
    }

}
