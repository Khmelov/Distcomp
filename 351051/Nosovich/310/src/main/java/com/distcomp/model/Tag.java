package com.distcomp.model;

/**
 * A label that may be attached to many issues (the "many" side of the
 * Issue-Tag many-to-many relation).
 */
public class Tag extends AbstractEntity {

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Tag{id=" + getId() + ", name='" + name + "'}";
    }
}
