package com.adashkevich.kafka.lab.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


public class MarkerRequestTo {
    @NotBlank @Size(min = 2, max = 32)
    public String name;
}
