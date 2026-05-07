package com.sergey.orsik.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreatorResponseTo {

    private Long id;
    private String login;
    private String firstname;
    private String lastname;
}
