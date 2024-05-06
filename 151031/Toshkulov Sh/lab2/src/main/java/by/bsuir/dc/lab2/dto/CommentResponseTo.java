package by.bsuir.dc.lab2.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponseTo {

    private BigInteger id;

    private BigInteger newsId;

    private String content;
}
