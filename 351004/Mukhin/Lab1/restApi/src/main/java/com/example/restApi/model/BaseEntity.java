package com.example.restApi.model;

import java.time.LocalDateTime;

public abstract class BaseEntity {
    protected Long id;
    protected LocalDateTime created;
    protected LocalDateTime modified;

    public BaseEntity() {
        this.created = LocalDateTime.now();
        this.modified = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    public LocalDateTime getModified() {
        return modified;
    }

    public void setModified(LocalDateTime modified) {
        this.modified = modified;
    }
}
