package by.bsuir.publisher.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@NoArgsConstructor
@Data
public class CommentActionDto {
    CommentActionTypeDto action;
    Object data;
}
