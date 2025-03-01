package com.dc.anotherone.model.dto;

public class MarkerRequestTo {
    private long id;
    private String name;

    public MarkerRequestTo(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public MarkerRequestTo(String name) {
        this.name = name;
    }

    private MarkerRequestTo() {
    }

    public long getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }
}
