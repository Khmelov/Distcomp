package com.lizaveta.notebook.model.dto.request;

import com.fasterxml.jackson.annotation.JsonRootName;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@JsonRootName("marker")
public record MarkerRequestTo(
        @NotBlank(message = "Name must not be blank")
        @Size(min = 2, max = 32, message = "Name length must be between 2 and 32")
        String name) {
}
