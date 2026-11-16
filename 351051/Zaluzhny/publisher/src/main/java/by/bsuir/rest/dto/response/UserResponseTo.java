package by.bsuir.rest.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponseTo {

    private Long id;
    private String login;
    private String password;
    private String firstname;
    private String lastname;
}
