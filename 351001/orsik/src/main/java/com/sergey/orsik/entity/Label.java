package com.sergey.orsik.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Label implements Identifiable {

    private Long id;
    private String name;
}
