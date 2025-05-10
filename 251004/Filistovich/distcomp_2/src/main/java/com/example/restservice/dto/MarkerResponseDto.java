package com.example.restservice.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class MarkerResponseDto implements Serializable {
    private Long id;
    private String name;
}
