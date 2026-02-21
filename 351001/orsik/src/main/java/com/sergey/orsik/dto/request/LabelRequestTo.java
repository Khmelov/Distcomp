package com.sergey.orsik.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LabelRequestTo {

    private Long id;

    @NotBlank(message = "name must not be blank")
    @Size(min = 2, max = 32)
    private String name;
}
