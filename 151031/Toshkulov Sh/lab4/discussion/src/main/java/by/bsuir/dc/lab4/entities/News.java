package by.bsuir.dc.lab4.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Date;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor

public class News {

    private Long id;

    private Long editorId;

    private String title;

    private String content;

    private Date created;

    private Date modified;
}
