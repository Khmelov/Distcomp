package by.bsuir.task310.mapper;

import by.bsuir.task310.domain.Notice;
import by.bsuir.task310.dto.request.NoticeRequestTo;
import by.bsuir.task310.dto.response.NoticeResponseTo;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface NoticeMapper {
    Notice toEntity(NoticeRequestTo dto);
    NoticeResponseTo toResponse(Notice entity);
}