package by.bsuir.dc.lab1.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Editor {

    private BigInteger id;

    private String login;

    private String password;

    private String firstname;

    private String lastname;
}
