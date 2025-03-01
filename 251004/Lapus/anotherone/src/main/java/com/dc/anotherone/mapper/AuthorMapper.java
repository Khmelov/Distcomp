package com.dc.anotherone.mapper;

import com.dc.anotherone.model.blo.Author;
import com.dc.anotherone.model.dto.AuthorRequestTo;
import com.dc.anotherone.model.dto.AuthorResponseTo;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.Collection;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AuthorMapper {

    Author mapToBlo(AuthorRequestTo authorRequestTo);

    AuthorResponseTo mapToDto(Author author);

    Collection<Author> mapToListBlo(Collection<AuthorRequestTo> kList);

    Collection<AuthorResponseTo> mapToListDto(Collection<Author> tList);

}
