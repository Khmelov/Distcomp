package org.rv.lab1.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record LoginResponseTo(
        @JsonProperty("access_token") String accessToken,
        @JsonProperty("token_type") String tokenType
) {
    public LoginResponseTo(String accessToken) {
        this(accessToken, "Bearer");
    }
}
