package by.bsuir.romamuhtasarov.impl.bean;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Comment {
    private long id;
    private String content;
    private long newsId;
}
