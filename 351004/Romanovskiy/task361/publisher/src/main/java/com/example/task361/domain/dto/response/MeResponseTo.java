package com.example.task361.domain.dto.response;

import com.example.task361.security.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MeResponseTo {
    private Long id;
    private String login;
    private Role role;
}
