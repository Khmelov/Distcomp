package by.bsuir.rest.mapper;

import by.bsuir.rest.dto.request.UserRequestTo;
import by.bsuir.rest.dto.response.UserResponseTo;
import by.bsuir.rest.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    User toEntity(UserRequestTo request);

    UserResponseTo toResponse(User user);

    @Mapping(target = "id", ignore = true)
    void update(UserRequestTo request, @MappingTarget User user);
}
