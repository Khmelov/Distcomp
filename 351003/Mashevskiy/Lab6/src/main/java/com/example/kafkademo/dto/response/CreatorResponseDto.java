package com.example.kafkademo.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CreatorResponseDto {
    private Long id;
    private String login;

    @JsonProperty("firstname")
    private String firstName;

    @JsonProperty("lastname")
    private String lastName;

    private String role;
    private LocalDateTime createdAt;
}