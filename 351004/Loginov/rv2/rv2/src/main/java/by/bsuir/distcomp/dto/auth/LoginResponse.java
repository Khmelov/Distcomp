package by.bsuir.distcomp.dto.auth;

public record LoginResponse(
        String access_token,
        String token_type
) {
}
