package com.lizaveta.notebook.model.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AccessTokenResponseTo(
        @JsonProperty("access_token")
        String accessToken,
        @JsonProperty("token_type")
        String tokenType) {
}
