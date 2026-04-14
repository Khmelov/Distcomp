package by.bsuir.distcomp.core.mapper;

import by.bsuir.distcomp.dto.request.MarkerRequestTo;
import by.bsuir.distcomp.dto.response.MarkerResponseTo;
import by.bsuir.distcomp.core.domain.Marker;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MarkerMapper {
    Marker toEntity(MarkerRequestTo dto);

    MarkerResponseTo toResponseDto(Marker entity);
}

