package com.example.kafkademo.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class RegistrationResponseDto {
    private Long id;
    private String login;

    @JsonProperty("firstname")
    private String firstname;

    @JsonProperty("lastname")
    private String lastname;

    private String role;

    @JsonProperty("createdAt")
    private LocalDateTime createdAt;

    public RegistrationResponseDto(com.example.kafkademo.entity.Creator creator) {
        this.id = creator.getId();
        this.login = creator.getLogin();
        this.firstname = creator.getFirstName();
        this.lastname = creator.getLastName();
        this.role = creator.getRole().name();
        this.createdAt = creator.getCreatedAt();
    }
}