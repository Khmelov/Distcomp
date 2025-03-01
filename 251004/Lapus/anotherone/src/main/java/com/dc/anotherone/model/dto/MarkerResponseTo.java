package com.dc.anotherone.model.dto;

public class MarkerResponseTo {
    private long id;
    private String name;

    public MarkerResponseTo(long id, String name) {
        this.id = id;
        this.name = name;
    }

    private MarkerResponseTo() {
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
