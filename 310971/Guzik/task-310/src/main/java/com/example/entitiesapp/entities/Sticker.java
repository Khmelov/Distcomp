package com.example.entitiesapp.entities;

import lombok.Getter;
import lombok.Setter;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class Sticker extends BaseEntity {
    private String name;
    private Set<Article> articles = new HashSet<>();
}