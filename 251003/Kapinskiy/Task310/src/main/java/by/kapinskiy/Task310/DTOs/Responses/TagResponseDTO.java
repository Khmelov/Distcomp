package by.kapinskiy.Task310.DTOs.Responses;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


public class TagResponseDTO {
    private Long id;
    private String name;

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
