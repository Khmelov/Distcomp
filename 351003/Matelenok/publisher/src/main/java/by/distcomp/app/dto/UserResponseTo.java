package by.distcomp.app.dto;
import java.util.List;
public record UserResponseTo(
        Long id,
        String login,
        String password,
        String firstname,
        String lastname,
        String role,
        List<Long> articleIds
) { }
