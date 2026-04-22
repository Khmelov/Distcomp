package by.bsuir.distcomp.mapper;

import by.bsuir.distcomp.dto.request.MarkRequestTo;
import by.bsuir.distcomp.dto.response.MarkResponseTo;
import by.bsuir.distcomp.entity.Mark;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface MarkMapper {
    @Mapping(target = "tweets", ignore = true)
    Mark toEntity(MarkRequestTo dto);

    MarkResponseTo toResponseDto(Mark entity);

    @Mapping(target = "tweets", ignore = true)
    void updateEntityFromDto(MarkRequestTo dto, @MappingTarget Mark entity);
}
