package com.example.rv.impl.creator;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class Creator {
    Long id;
    String login;
    String password;
    String firstname;
    String lastname;
}
