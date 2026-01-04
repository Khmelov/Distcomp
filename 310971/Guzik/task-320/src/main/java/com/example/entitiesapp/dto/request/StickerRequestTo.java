package com.example.entitiesapp.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StickerRequestTo {
    @Size(min = 2, max = 32, message = "Name must be between 2 and 32 characters")
    private String name;
}