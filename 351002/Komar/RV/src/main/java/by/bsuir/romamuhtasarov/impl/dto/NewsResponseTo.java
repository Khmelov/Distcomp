package by.bsuir.romamuhtasarov.impl.dto;

import lombok.Getter;
import lombok.Setter;

import java.sql.Date;

@Setter
@Getter
public class NewsResponseTo {
    private long id;
    private long writerId;
    private String title;
    private String content;
    private Date created;
    private Date modified;
}

