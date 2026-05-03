package org.discussion.other.enums;

public enum RequestMethod {

    GET("get"),
    POST("post"),
    DELETE("delete"),
    PUT("put");

    private String name;

    RequestMethod(String name) {
        this.name = name;
    }

}
