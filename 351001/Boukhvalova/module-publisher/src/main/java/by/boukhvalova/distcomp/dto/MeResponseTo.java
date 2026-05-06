package by.boukhvalova.distcomp.dto;

import by.boukhvalova.distcomp.entities.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MeResponseTo {
    private long id;
    private String login;
    private UserRole role;
}

