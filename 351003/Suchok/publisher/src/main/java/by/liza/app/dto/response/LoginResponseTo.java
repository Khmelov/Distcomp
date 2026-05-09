package by.liza.app.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record LoginResponseTo(
        @JsonProperty("access_token") String accessToken,
        @JsonProperty("token_type")   String tokenType
) {}
