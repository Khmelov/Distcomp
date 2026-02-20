package by.bsuir.distcomp.mapper;

import by.bsuir.distcomp.dto.request.MarkRequestTo;
import by.bsuir.distcomp.dto.response.MarkResponseTo;
import by.bsuir.distcomp.entity.Mark;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface MarkMapper {
    Mark toEntity(MarkRequestTo dto);
    MarkResponseTo toResponseDto(Mark entity);
    void updateEntityFromDto(MarkRequestTo dto, @MappingTarget Mark entity);
}
