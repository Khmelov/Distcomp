package com.bsuir.distcomp.mapper;

import com.bsuir.distcomp.dto.TopicRequestTo;
import com.bsuir.distcomp.dto.TopicResponseTo;
import com.bsuir.distcomp.entity.Marker;
import com.bsuir.distcomp.entity.Topic;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring")
public interface TopicMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "created", ignore = true)
    @Mapping(target = "modified", ignore = true)
    @Mapping(target = "writer", ignore = true)
    @Mapping(target = "markers", source = "markers", qualifiedByName = "mapMarkers")
    Topic toEntity(TopicRequestTo dto);

    @Mapping(source = "writer.id", target = "writerId")
    TopicResponseTo toDto(Topic entity);

    @Named("mapMarkers")
    default List<Marker> mapMarkers(List<String> markerNames) {
        if (markerNames == null) {
            return null;
        }
        List<Marker> markers = new ArrayList<>();
        for (String name : markerNames) {
            Marker marker = new Marker();
            marker.setName(name);
            markers.add(marker);
        }
        return markers;
    }
}