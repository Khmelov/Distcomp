package com.homel.user_stories.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class LabelRequestTo {
    private Long id;

    @NotBlank
    @Size(min = 2, max = 32)
    private String name;
}
