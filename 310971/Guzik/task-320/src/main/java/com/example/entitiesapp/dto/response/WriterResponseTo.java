package com.example.entitiesapp.dto.response;

import com.example.entitiesapp.dto.BaseDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WriterResponseTo extends BaseDto {
    private String login;  // Убираем @JsonProperty("writer")
    private String firstname;
    private String lastname;
}