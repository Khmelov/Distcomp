package com.example.restapp.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class Sticker extends BaseEntity {
    private String name;
}