package com.example.lab.publisher.security;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public final class AuthDtos {
    private AuthDtos() {
    }

    public static class RegisterRequest {
        @NotBlank
        @Size(min = 2, max = 64)
        private final String login;

        @NotBlank
        @Size(min = 8, max = 128)
        private final String password;

        @NotBlank
        @Size(min = 2, max = 64)
        private final String firstname;

        @NotBlank
        @Size(min = 2, max = 64)
        private final String lastname;

        private final Role role;

        @JsonCreator
        public RegisterRequest(
                @JsonProperty("login") String login,
                @JsonProperty("password") String password,
                @JsonProperty("firstname") String firstname,
                @JsonProperty("lastname") String lastname,
                @JsonProperty("role") Role role) {
            this.login = login;
            this.password = password;
            this.firstname = firstname;
            this.lastname = lastname;
            this.role = role == null ? Role.CUSTOMER : role;
        }

        public String getLogin() {
            return login;
        }

        public String getPassword() {
            return password;
        }

        public String getFirstname() {
            return firstname;
        }

        public String getLastname() {
            return lastname;
        }

        public Role getRole() {
            return role;
        }
    }

    public static class LoginRequest {
        @NotBlank
        private final String login;

        @NotBlank
        private final String password;

        @JsonCreator
        public LoginRequest(
                @JsonProperty("login") String login,
                @JsonProperty("password") String password) {
            this.login = login;
            this.password = password;
        }

        public String getLogin() {
            return login;
        }

        public String getPassword() {
            return password;
        }
    }

    public static class TokenResponse {
        private final String access_token;
        private final String token_type;

        public TokenResponse(String accessToken) {
            this.access_token = accessToken;
            this.token_type = "Bearer";
        }

        public String getAccess_token() {
            return access_token;
        }

        public String getToken_type() {
            return token_type;
        }
    }

    public static class CurrentUserResponse {
        private final Long id;
        private final String login;
        private final String firstname;
        private final String lastname;
        private final Role role;

        public CurrentUserResponse(Long id, String login, String firstname, String lastname, Role role) {
            this.id = id;
            this.login = login;
            this.firstname = firstname;
            this.lastname = lastname;
            this.role = role;
        }

        public Long getId() {
            return id;
        }

        public String getLogin() {
            return login;
        }

        public String getFirstname() {
            return firstname;
        }

        public String getLastname() {
            return lastname;
        }

        public Role getRole() {
            return role;
        }
    }
}

