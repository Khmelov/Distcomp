package by.bsuir.dc.lab4.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommentRequestTo implements DtoBase {

    private Long id = Math.abs(UUID.randomUUID().getLeastSignificantBits());

    private Long newsId;

    private String content = "";
}
