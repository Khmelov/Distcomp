package by.bsuir.romamuhtasarov.impl.bean;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Writer {
    private long id;
    private String login;
    private String password;
    private String firstname;
    private String lastname;
}