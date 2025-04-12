package by.bsuir.distcomp.dto.request;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReactionRequestTo {

    private Long id;

    private Long articleId;

    @Size(min = 2, max = 2048)
    private String content;

}
