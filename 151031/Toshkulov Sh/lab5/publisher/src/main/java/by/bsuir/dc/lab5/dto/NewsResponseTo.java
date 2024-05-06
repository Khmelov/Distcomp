package by.bsuir.dc.lab5.dto;

import by.bsuir.dc.lab5.kafka.DtoBase;
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
public class NewsResponseTo implements DtoBase {

    private Long id = 0L;

    private BigInteger editorId;

    private String title;

    private String content;

    private Date created;

    private Date modified;
}
