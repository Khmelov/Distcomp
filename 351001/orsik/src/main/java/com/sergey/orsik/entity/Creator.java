package com.sergey.orsik.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Creator implements Identifiable {

    private Long id;
    private String login;
    private String password;
    private String firstname;
    private String lastname;
}
