package by.bsuir.distcomp.dto.response;

import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonRootName("marker")
public class MarkerResponseTo {

    private Long id;
    private String Name;

}
