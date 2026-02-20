package by.bsuir.distcomp.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class MarkRequestTo {
    private Long id;

    @NotBlank(message = "Name must not be blank")
    @Size(min = 2, max = 32, message = "Name must be between 2 and 32 characters")
    private String name;

    public MarkRequestTo() {}

    public MarkRequestTo(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
