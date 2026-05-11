package com.adashkevich.rest.lab.model;

public abstract class BaseEntity {
    private Long id;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
}
