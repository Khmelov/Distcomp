package by.bsuir.rest.mapper;

import by.bsuir.rest.dto.request.MarkerRequestTo;
import by.bsuir.rest.dto.response.MarkerResponseTo;
import by.bsuir.rest.entity.Marker;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface MarkerMapper {

    @Mapping(target = "id", ignore = true)
    Marker toEntity(MarkerRequestTo request);

    MarkerResponseTo toResponse(Marker marker);

    @Mapping(target = "id", ignore = true)
    void update(MarkerRequestTo request, @MappingTarget Marker marker);
}
