package by.egorsosnovski.distcomp.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreatorRequestTo {
    long id;

    @Size(min = 2, max = 64)
    String login;

    @Size(min = 8, max = 128)
    String password;

    @Size(min = 2, max = 64)
    String firstname;

    @Size(min = 2, max = 64)
    String lastname;
}