package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostRequestTo implements Serializable {
    private static final long serialVersionUID = 1L;

    private Object id; // Object, чтобы точно проглотить и число, и строку
    private Object newsId;
    private String content;
}