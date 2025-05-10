package com.homel.user_stories.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class UserResponseTo {
    private Long id;
    private String login;
    private String firstname;
    private String lastname;
}
