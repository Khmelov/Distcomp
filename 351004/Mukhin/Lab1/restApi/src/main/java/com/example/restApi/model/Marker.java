package com.example.restApi.model;

public class Marker extends BaseEntity {
    private String name;

    public Marker() {
        super();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
