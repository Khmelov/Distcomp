package by.bsuir.task320.mapper;

import by.bsuir.task310.domain.Creator;
import by.bsuir.task310.dto.request.CreatorRequestTo;
import by.bsuir.task310.dto.response.CreatorResponseTo;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CreatorMapper {
    Creator toEntity(CreatorRequestTo dto);
    CreatorResponseTo toResponse(Creator entity);
}