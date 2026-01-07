package com.group310971.gormash.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MarkRequestTo {

    private Long id;

    @Size(min = 2, max = 32, message = "Name must be between 2 and 32 characters")
    private String name;
}
