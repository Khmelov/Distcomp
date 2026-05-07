package by.distcomp.app.dto;
import java.util.List;
public record UserResponseTo(
        Long id,
        String login,
        String firstname,
        String lastname,
        List<Long> articleIds
) { }
