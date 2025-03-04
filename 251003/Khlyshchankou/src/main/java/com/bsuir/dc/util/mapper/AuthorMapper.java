package com.bsuir.dc.util.mapper;

import com.bsuir.dc.dto.request.AuthorRequestTo;
import com.bsuir.dc.dto.response.AuthorResponseTo;
import com.bsuir.dc.model.Author;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AuthorMapper {
    AuthorResponseTo toCreatorResponse(Author author);
    List<AuthorResponseTo> toAuthorResponseList(List<Author> authors);
    Author toAuthor(AuthorRequestTo authorRequestTo);
}
