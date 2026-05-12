package com.bsuir.distcomp.dto;

import lombok.Data;

@Data
public class WriterResponseTo {

    private Long id;
    private String login;
    private String firstname;
    private String lastname;
    private String role;
}