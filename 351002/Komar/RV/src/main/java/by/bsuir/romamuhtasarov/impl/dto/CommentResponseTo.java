package by.bsuir.romamuhtasarov.impl.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CommentResponseTo {
        private long id;
        private String content;
        private long newsId;
}
