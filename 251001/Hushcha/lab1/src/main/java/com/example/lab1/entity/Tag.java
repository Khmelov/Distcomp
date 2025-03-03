package com.example.lab1.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class Tag implements BaseEntity {

    private Long id;

    private String name;
}
