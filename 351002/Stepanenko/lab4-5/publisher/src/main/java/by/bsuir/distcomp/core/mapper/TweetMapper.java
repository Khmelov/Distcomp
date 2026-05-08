package by.bsuir.distcomp.core.mapper;

import by.bsuir.distcomp.dto.request.TweetRequestTo;
import by.bsuir.distcomp.dto.response.TweetResponseTo;
import by.bsuir.distcomp.core.domain.Tweet;
import by.bsuir.distcomp.core.domain.Marker;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface TweetMapper {

    @Mapping(target = "author", ignore = true)
    @Mapping(target = "markers", ignore = true)
    @Mapping(target = "created", ignore = true)
    @Mapping(target = "modified", ignore = true)
    Tweet toEntity(TweetRequestTo dto);

    @Mapping(target = "authorId", source = "author.id")
    @Mapping(target = "markerIds", expression = "java(mapMarkersToIds(entity.getMarkers()))")
    TweetResponseTo toResponseDto(Tweet entity);

    default Set<Long> mapMarkersToIds(Set<Marker> markers) {
        if (markers == null) return null;
        return markers.stream().map(Marker::getId).collect(Collectors.toSet());
    }
}