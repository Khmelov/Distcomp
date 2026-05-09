package com.example.task350.domain.dto.response;

import java.io.Serializable;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MarkerResponseTo implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;
    private String name;
}