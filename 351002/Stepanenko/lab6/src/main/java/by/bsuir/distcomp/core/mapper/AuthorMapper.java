package by.bsuir.distcomp.core.mapper;

import by.bsuir.distcomp.dto.request.AuthorRequestTo;
import by.bsuir.distcomp.dto.response.AuthorResponseTo;
import by.bsuir.distcomp.core.domain.Author;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AuthorMapper {

    @Mapping(target = "tweets", ignore = true)
    Author toEntity(AuthorRequestTo dto);

    AuthorResponseTo toResponseDto(Author entity);
}