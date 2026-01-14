package org.example;

public class TagShortResponseTo {

    private Long id;
    private String name;

    public TagShortResponseTo(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public TagShortResponseTo() {

    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }
}
