package by.bsuir.distcomp.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MarkerResponseTo {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("name")
    private String name;

    public MarkerResponseTo() {}

    public MarkerResponseTo(Long id, String name) {
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
}
