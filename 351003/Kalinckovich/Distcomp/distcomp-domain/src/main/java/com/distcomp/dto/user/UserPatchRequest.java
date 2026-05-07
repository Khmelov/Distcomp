package com.distcomp.dto.user;

import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class UserPatchRequest {

    @Size(min = 2, max = 64)
    private String login;

    @Size(min = 2, max = 64)
    private String firstname;

    @Size(min = 2, max = 64)
    private String lastname;

    @Size(min = 8, max = 128)
    private String password;
}
