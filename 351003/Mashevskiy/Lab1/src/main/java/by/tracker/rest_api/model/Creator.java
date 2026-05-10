package by.tracker.rest_api.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Creator {
    private Long id;
    private String login;
    private String password;
    private String firstName;
    private String lastName;
}