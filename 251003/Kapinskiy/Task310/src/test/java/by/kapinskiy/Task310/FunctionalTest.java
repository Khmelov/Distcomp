package by.kapinskiy.Task310;

import io.restassured.RestAssured;

import org.junit.Before;
import org.junit.BeforeClass;

public class FunctionalTest {
    @BeforeClass
    public static void setup() {
        RestAssured.port = 24110;
        RestAssured.basePath = "/api/v1.0";
        RestAssured.baseURI = "http://localhost";
    }
}
