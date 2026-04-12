package by.distcomp.app.mapper;

import by.distcomp.app.model.User;
import by.distcomp.app.dto.UserRequestTo;
import by.distcomp.app.dto.UserResponseTo;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toEntity(UserRequestTo dto);

    UserResponseTo toResponse(User user);
}
