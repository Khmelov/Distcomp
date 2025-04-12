package by.bsuir.distcomp.dto.mapper;

import by.bsuir.distcomp.dto.request.MarkerRequestTo;
import by.bsuir.distcomp.dto.response.MarkerResponseTo;
import by.bsuir.distcomp.entity.Marker;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class MarkerMapper {
    public abstract MarkerResponseTo toDto(Marker marker);
    public abstract Marker toEntity(MarkerRequestTo markerDTO);
}
