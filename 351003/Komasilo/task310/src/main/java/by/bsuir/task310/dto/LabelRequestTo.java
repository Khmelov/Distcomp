package by.bsuir.task310.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class LabelRequestTo {
    private Long id;

    @NotBlank
    @Size(min = 2, max = 32)
    private String name;
}