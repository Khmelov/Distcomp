package by.boukhvalova.distcomp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TokenResponseTo {
    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("type_token")
    private String typeToken;
}

