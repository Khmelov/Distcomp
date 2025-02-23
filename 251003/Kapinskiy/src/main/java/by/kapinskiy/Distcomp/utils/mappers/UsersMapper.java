package by.kapinskiy.Distcomp.utils.mappers;

import by.kapinskiy.Distcomp.DTOs.Requests.UserRequestDTO;
import by.kapinskiy.Distcomp.DTOs.Responses.UserResponseDTO;
import by.kapinskiy.Distcomp.models.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UsersMapper {
    UserResponseDTO toUserResponse(User user);
    List<UserResponseDTO> toUserResponseList(List<User> users);
    User toUser(UserRequestDTO userRequestDTO);
}
