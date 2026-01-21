package com.example.app.dto.cache;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CachedAuthorDTO implements Serializable {
    private Long id;
    private String login;
    private String password;
    private String firstname;
    private String lastname;
}