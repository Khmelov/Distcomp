package org.ex.distributed_computing.mapper;

import org.ex.distributed_computing.dto.request.UserRequestDTO;
import org.ex.distributed_computing.dto.response.UserResponseDTO;
import org.ex.distributed_computing.model.User;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring",
    injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public interface UserMapper {

  User toEntity(UserRequestDTO dto);

  @Mapping(target = "password", ignore = true)
  UserResponseDTO toDto(User author);

  List<UserResponseDTO> toDtoList(List<User> authors);
}
