package com.example.entitiesapp.entities;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public abstract class BaseEntity {
    private Long id;
    private LocalDateTime created;
    private LocalDateTime modified;
}