package by.bsuir.task330.publisher.mapper;

import by.bsuir.task330.publisher.domain.Creator;
import by.bsuir.task330.publisher.dto.request.CreatorRequestTo;
import by.bsuir.task330.publisher.dto.response.CreatorResponseTo;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CreatorMapper {
    Creator toEntity(CreatorRequestTo requestTo);
    CreatorResponseTo toResponse(Creator creator);
}
