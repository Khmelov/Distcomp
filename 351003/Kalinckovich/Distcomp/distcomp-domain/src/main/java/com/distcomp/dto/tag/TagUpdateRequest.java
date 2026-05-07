package com.distcomp.dto.tag;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class TagUpdateRequest {
    @Size(min = 2, max = 32, message = "Tag name size must be in range from 2 to 32 ")
    private String name;
}
