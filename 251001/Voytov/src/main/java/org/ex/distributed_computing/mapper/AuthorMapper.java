package org.ex.distributed_computing.mapper;

import java.util.List;
import org.ex.distributed_computing.dto.request.AuthorRequestDTO;
import org.ex.distributed_computing.dto.response.AuthorResponseDTO;
import org.ex.distributed_computing.model.Author;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring",
    injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public interface AuthorMapper {

  Author toEntity(AuthorRequestDTO dto);

  @Mapping(target = "password", ignore = true)
  AuthorResponseDTO toDto(Author author);

  List<AuthorResponseDTO> toDtoList(List<Author> authors);

}
