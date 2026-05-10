package by.liza.app.dto.response;

import lombok.Data;
import java.io.Serializable;

@Data
public class MarkResponseTo implements Serializable {
    private Long id;
    private String name;
}