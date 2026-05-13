package com.example.publisher.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class StickerRequestTo {

    @Size(min = 2, max = 32)
    private String name;
}