package by.kopvzakone.distcomp.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class EditorResponseTo {
    long id;

    @Size(min = 2, max = 64)
    String login;

    @Size(min = 2, max = 64)
    String firstname;

    @Size(min = 2, max = 64)
    String lastname;
}
