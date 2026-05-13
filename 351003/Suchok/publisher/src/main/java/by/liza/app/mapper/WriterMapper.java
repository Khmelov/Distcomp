package by.liza.app.mapper;

import by.liza.app.dto.request.WriterRequestTo;
import by.liza.app.dto.response.WriterResponseTo;
import by.liza.app.model.Writer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface WriterMapper {

    @Mapping(target = "articles", ignore = true)
    @Mapping(target = "role",     ignore = true)
    @Mapping(target = "password", ignore = true)
    Writer toEntity(WriterRequestTo requestTo);

    WriterResponseTo toResponse(Writer writer);

    List<WriterResponseTo> toResponseList(List<Writer> writers);

    @Mapping(target = "articles", ignore = true)
    @Mapping(target = "role",     ignore = true)
    @Mapping(target = "password", ignore = true)
    void updateEntityFromRequest(WriterRequestTo requestTo, @MappingTarget Writer writer);
}
