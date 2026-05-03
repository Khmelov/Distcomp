package by.liza.app.mapper;

import by.liza.app.dto.request.MarkRequestTo;
import by.liza.app.dto.response.MarkResponseTo;
import by.liza.app.model.Mark;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MarkMapper {

    @Mapping(target = "articles", ignore = true)
    Mark toEntity(MarkRequestTo requestTo);

    MarkResponseTo toResponse(Mark mark);

    List<MarkResponseTo> toResponseList(List<Mark> marks);
}