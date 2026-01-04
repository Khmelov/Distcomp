package com.example.entitiesapp.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public abstract class BaseDto {
    private Long id;
    private LocalDateTime created;
    private LocalDateTime modified;
}