package by.bsuir.distcomp.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class MarkerRequestTo {
    private Long id;

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 32, message = "Name must be between 2 and 32 characters")
    @JsonProperty("name")
    private String name;

    public MarkerRequestTo() {}

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
