package com.example.demo.labrest.mapper;

import com.example.demo.labrest.dto.*;
import com.example.demo.labrest.model.Creator;
import com.example.demo.labrest.model.Marker;
import com.example.demo.labrest.model.Notice;
import com.example.demo.labrest.model.Topic;
import org.mapstruct.*;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface AppMapper {
    Creator toCreator(CreatorRequestTo req);
    CreatorResponseTo toCreatorResponse(Creator src);

    @Mapping(target = "markers", ignore = true)
    @Mapping(target = "markerIds", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "creator", ignore = true)
    @Mapping(target = "created", ignore = true)
    @Mapping(target = "modified", ignore = true)
    Topic toTopic(TopicRequestTo req);

    Marker toMarker(MarkerRequestTo req);
    MarkerResponseTo toMarkerResponse(Marker src);

    Notice toNotice(NoticeRequestTo req);
    NoticeResponseTo toNoticeResponse(Notice src);

    @Mapping(target = "creatorId", source = "creator.id")
    @Mapping(target = "markerIds", expression = "java(src.getMarkers().stream().map(Marker::getId).collect(java.util.stream.Collectors.toSet()))")
    TopicResponseTo toTopicResponseWithRelations(Topic src);
}