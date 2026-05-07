package by.bsuir.distcomp.entity;

import java.util.Objects;

public class Marker {
    private Long id;
    private String name;

    public Marker() {}

    public Marker(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Marker marker = (Marker) o;
        return Objects.equals(id, marker.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
