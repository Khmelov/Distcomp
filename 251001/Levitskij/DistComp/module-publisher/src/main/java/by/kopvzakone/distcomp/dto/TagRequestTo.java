package by.kopvzakone.distcomp.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TagRequestTo {
    long id;
    @Size(min = 2, max = 32)
    String name;
}
