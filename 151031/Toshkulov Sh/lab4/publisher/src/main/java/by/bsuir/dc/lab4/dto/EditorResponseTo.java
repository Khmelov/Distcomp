package by.bsuir.dc.lab4.dto;

import by.bsuir.dc.lab4.kafka.DtoBase;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EditorResponseTo implements DtoBase {

    private Long id = 0L;

    private String login;

    private String firstname;

    private String lastname;
}
