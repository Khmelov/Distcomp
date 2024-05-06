package by.bsuir.dc.lab5.dto;

import by.bsuir.dc.lab5.kafka.DtoBase;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MarkerResponseTo implements DtoBase {

    private Long id = 0L;

    private String name;
}
