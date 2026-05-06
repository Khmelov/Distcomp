package by.boukhvalova.distcomp.dto;

import by.boukhvalova.distcomp.entities.User;
import org.mapstruct.*;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {

    UserResponseTo out(User user);

    User in(UserRequestTo user);
}
