package com.group310971.gormash.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EditorResponseTo {
    private Long id;
    private String login;
    private String firstname;
    private String lastname;
}