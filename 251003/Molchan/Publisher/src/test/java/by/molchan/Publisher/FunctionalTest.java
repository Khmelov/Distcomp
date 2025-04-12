package by.molchan.Publisher;

import io.restassured.RestAssured;

import org.junit.BeforeClass;

public class FunctionalTest {
    @BeforeClass
    public static void setup() {
        RestAssured.port = 24110;
        RestAssured.basePath = "/api/v1.0";
        RestAssured.baseURI = "http://localhost";
    }
}
