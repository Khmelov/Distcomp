package com.github.Lexya06.startrestapp;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class FunctionalTest {

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
}
