package by.bsuir.dc.lab4.dto;


import by.bsuir.dc.lab4.kafka.DtoBase;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommentRequestTo implements DtoBase {

    private Long id = 0L;

    private BigInteger newsId;

    private String content = "";
}
