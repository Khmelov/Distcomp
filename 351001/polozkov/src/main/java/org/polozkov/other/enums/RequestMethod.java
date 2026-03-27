package org.polozkov.other.enums;

public enum RequestMethod {

    GET("get"),
    POST("post"),
    DELETE("delete"),
    PUT("put"),
    PATCH("patch");

    private String name;

    RequestMethod(String name) {
        this.name = name;
    }

}
