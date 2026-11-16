package by.bsuir.rest.mapper;

import by.bsuir.rest.dto.request.StoryRequestTo;
import by.bsuir.rest.dto.response.StoryResponseTo;
import by.bsuir.rest.entity.Story;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface StoryMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "created", ignore = true)
    @Mapping(target = "modified", ignore = true)
    @Mapping(target = "markerIds", ignore = true)
    @Mapping(target = "markers", ignore = true)
    Story toEntity(StoryRequestTo request);

    @Mapping(target = "markerIds", ignore = true)
    StoryResponseTo toResponse(Story story);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "created", ignore = true)
    @Mapping(target = "modified", ignore = true)
    @Mapping(target = "markerIds", ignore = true)
    @Mapping(target = "markers", ignore = true)
    void update(StoryRequestTo request, @MappingTarget Story story);
}
