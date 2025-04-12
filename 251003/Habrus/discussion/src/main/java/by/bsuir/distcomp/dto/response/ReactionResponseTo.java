package by.bsuir.distcomp.dto.response;

import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonRootName("reaction")
public class ReactionResponseTo {

    private Long id;
    private Long articleId;
    private String content;

}
