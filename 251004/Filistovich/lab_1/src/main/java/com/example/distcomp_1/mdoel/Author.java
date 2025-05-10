package com.example.distcomp_1.mdoel;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter(AccessLevel.PUBLIC)
@Setter(AccessLevel.PUBLIC)
public class Author {
    long id;
    String login;
    String password;
    String firstname;
    String lastname;

    @Getter(AccessLevel.PUBLIC)
    @Setter(AccessLevel.PUBLIC)
    public static class In{
        @Positive
        @Nullable
        Long id;
        @Size(min=2, max=64)
        String login;
        @Size(min = 8, max = 128)
        String password;
        @Size(min = 2, max = 64)
        String firstname;
        @Size(min = 2, max = 64)
        String lastname;
    }

    @Getter(AccessLevel.PUBLIC)
    @Setter(AccessLevel.PUBLIC)
    public static class Out{
        Long id;
        String login;
        String firstname;
        String lastname;
    }


}
