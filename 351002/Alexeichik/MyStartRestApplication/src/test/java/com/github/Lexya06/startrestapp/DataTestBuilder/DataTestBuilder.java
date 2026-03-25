package com.github.Lexya06.startrestapp.DataTestBuilder;

import org.apache.http.HttpStatus;

import static io.restassured.RestAssured.given;

public abstract class DataTestBuilder {
    abstract public String getUpdateBody();
    abstract public String getCreateBody();
    abstract public String getInvalidBody();
    public String getContentType(){
        return "application/json";
    }
    public String getIdName(){
        return "id";
    }
    public String getEntityPath(){
        return getEntitiesPath() + "/%d";
    }
    public String getEntityPath(Integer id){
        return String.format(getEntityPath(), id);
    }
    public abstract String getEntitiesPath();
    public Integer getCreatedEntityId(){
        return given().contentType(getContentType()).body(getCreateBody()).when().post(getEntitiesPath()).then().statusCode(HttpStatus.SC_CREATED).extract().path(getIdName());
    }
}
