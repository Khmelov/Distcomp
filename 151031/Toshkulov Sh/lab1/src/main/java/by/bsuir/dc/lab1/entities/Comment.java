package by.bsuir.dc.lab1.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Comment {
    private BigInteger id;

    private BigInteger newsId;

    private String content;
}
