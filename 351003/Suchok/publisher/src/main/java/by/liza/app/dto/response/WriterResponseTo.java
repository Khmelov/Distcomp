package by.liza.app.dto.response;

import lombok.Data;
import java.io.Serializable;

@Data
public class WriterResponseTo implements Serializable {
    private Long id;
    private String login;
    private String firstname;
    private String lastname;
}