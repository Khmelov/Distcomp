package by.tracker.rest_api.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
public class MarkerRequestTo {
    private Long id;

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 32, message = "Marker name must be between 2 and 32 characters")
    private String name;
}