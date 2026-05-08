package by.liza.app.dto.response;

import lombok.Data;
import java.io.Serializable;

@Data
public class NoteResponseTo implements Serializable {
    private Long id;
    private Long articleId;
    private String content;
    private String state;
}