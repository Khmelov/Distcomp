package com.example.demo.dto.response;

import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.LowerCamelCaseStrategy.class)
@JsonRootName("writer")
public class WriterResponseTo {
    private Long id;
    private String login;
    //private String password;
    private String firstname;
    private String lastname;
}
