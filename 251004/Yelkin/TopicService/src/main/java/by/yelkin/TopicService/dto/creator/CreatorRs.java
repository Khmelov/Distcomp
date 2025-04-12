package by.yelkin.TopicService.dto.creator;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreatorRs {
    private Long id;
    private String login;
    private String password;
    private String firstname;
    private String lastname;
}
