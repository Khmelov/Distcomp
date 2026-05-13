package org.example.dto;

import lombok.*;
import org.example.model.PostState;

@Data
@NoArgsConstructor
@AllArgsConstructor // Этот конструктор теперь будет принимать PostState
public class PostMessage {
    private Long id;
    private Long newsId;
    private String content;
    private PostState state; // Проверь, чтобы здесь не было String
}