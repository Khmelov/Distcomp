package by.bsuir.dc.lab1.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;
import java.sql.Date;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class News {

    private BigInteger id;

    private BigInteger editorId;

    private String title;

    private String content;

    private Date created;

    private Date modified;
}
