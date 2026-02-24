package org.example.newsapi.dto.response;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.Data;

@Data
@JsonRootName("user") // Если тест требует обертку
public class UserResponseTo {
    private Long id;
    private String login;
    private String firstname;
    private String lastname;
}