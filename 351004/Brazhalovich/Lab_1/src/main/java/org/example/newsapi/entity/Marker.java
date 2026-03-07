package org.example.newsapi.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class Marker extends BaseEntity {
    private String name;
}