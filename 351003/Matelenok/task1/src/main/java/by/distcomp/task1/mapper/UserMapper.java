package by.distcomp.task1.mapper;

import by.distcomp.task1.dto.UserRequestTo;
import by.distcomp.task1.dto.UserResponseTo;
import by.distcomp.task1.model.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toEntity(UserRequestTo dto);

    UserResponseTo toResponse(User user);
}
