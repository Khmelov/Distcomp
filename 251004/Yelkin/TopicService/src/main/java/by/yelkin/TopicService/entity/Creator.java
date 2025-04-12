package by.yelkin.TopicService.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Creator extends BaseEntity {
    private String login;
    private String password;
    private String firstname;
    private String lastname;
}
