package by.bsuir.rest.dto.response;

import by.bsuir.rest.dto.MessageState;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageResponseTo {

    private Long id;
    private Long storyId;
    private String content;
    private MessageState state;
}
