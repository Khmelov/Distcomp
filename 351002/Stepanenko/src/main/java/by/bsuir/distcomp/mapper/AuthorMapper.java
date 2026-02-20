package by.bsuir.distcomp.mapper;

import by.bsuir.distcomp.dto.request.AuthorRequestTo;
import by.bsuir.distcomp.dto.response.AuthorResponseTo;
import by.bsuir.distcomp.entity.Author;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AuthorMapper {
    Author toEntity(AuthorRequestTo dto);

    AuthorResponseTo toResponseDto(Author entity);
}
