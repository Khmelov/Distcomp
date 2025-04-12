package by.bsuir.distcomp.dto.request;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MarkerRequestTo {

    private Long id;

    @Size(min = 2, max = 32)
    private String Name;

}
