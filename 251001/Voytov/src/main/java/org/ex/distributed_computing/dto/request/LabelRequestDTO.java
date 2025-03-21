package org.ex.distributed_computing.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record LabelRequestDTO(

    Long id,

    @NotNull
    @Size(min = 2, max = 32)
    String name
) {

}
