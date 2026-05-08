package com.example.task361.domain.dto.response;
import java.io.Serializable;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthorResponseTo implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;
    private String login;
    private String firstname;
    private String lastname;
}