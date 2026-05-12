package by.tracker.rest_api.dto;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
public class CreatorResponseTo {
    private Long id;
    private String login;

    @JsonProperty("firstname")  // ← добавляем
    private String firstName;

    @JsonProperty("lastname")   // ← добавляем
    private String lastName;
}