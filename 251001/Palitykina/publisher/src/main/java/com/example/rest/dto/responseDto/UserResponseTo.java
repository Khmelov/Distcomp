package com.example.rest.dto.responseDto;

import lombok.Data;


@Data
public class UserResponseTo {

    private Long id;
    private String login;
    private String firstname;
    private String lastname;

}
