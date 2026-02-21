package com.lizaveta.notebook.model.entity;

public final class Marker {

    private final Long id;
    private final String name;

    public Marker(final Long id, final String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Marker withId(final Long newId) {
        return new Marker(newId, name);
    }

    public Marker withName(final String newName) {
        return new Marker(id, newName);
    }
}
