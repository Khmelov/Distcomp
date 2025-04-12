package com.example.rest.dto.updateDto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LabelUpdateTo {
    private long id;
    @Size(min = 2, max = 32)
    private String name;
}


