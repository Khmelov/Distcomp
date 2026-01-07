package com.group310971.gormash.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MarkResponseTo {

    private Long id;
    private String name;
    private String color;
    private Integer weight;
}
