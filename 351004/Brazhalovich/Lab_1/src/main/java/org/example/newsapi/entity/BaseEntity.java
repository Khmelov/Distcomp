package org.example.newsapi.entity;

import lombok.Data;
import java.io.Serializable;

@Data
public abstract class BaseEntity implements Serializable {
    private Long id;
}