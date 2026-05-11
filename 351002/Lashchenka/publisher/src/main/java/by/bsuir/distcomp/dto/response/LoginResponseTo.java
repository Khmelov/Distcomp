package by.bsuir.distcomp.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LoginResponseTo {

    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("type_token")
    private String typeToken = "Bearer";

    public LoginResponseTo() {}

    public LoginResponseTo(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getTypeToken() {
        return typeToken;
    }

    public void setTypeToken(String typeToken) {
        this.typeToken = typeToken;
    }
}
